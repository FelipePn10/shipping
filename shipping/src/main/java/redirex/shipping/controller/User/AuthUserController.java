package redirex.shipping.controller.User;

import io.jsonwebtoken.JwtException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import redirex.shipping.dto.request.AuthUserRequest;
import redirex.shipping.dto.response.AuthUserResponse;
import redirex.shipping.security.JwtUtil;
import redirex.shipping.service.TokenBlacklistService;
import redirex.shipping.service.UserServiceImpl;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/public/auth/v1")
@RequiredArgsConstructor
public class AuthUserController {
    private static final Logger logger = LoggerFactory.getLogger(AuthUserController.class);

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final TokenBlacklistService tokenBlacklistService;
    private final UserServiceImpl userService;

    @PostMapping("/user/login")
    public ResponseEntity<?> login(@Valid @RequestBody AuthUserRequest authRequest) {
        logger.info("Tentativa de login para email: {}", authRequest.getEmail());
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            authRequest.getEmail(),
                            authRequest.getPassword()
                    )
            );
            UUID userId = userService.findUserIdByEmail(authRequest.getEmail());
            String token = jwtUtil.generateToken(authRequest.getEmail(), userId);
            AuthUserResponse response = AuthUserResponse.builder()
                    .token(token)
                    .userId(userId)
                    .build();
            logger.info("Login bem-sucedido para email: {}", authRequest.getEmail());
            return ResponseEntity.ok(response);
        } catch (BadCredentialsException e) {
            logger.warn("Credenciais inválidas para email: {}", authRequest.getEmail());
            return buildErrorResponse(HttpStatus.UNAUTHORIZED, "Credenciais inválidas");
        } catch (Exception e) {
            logger.error("Erro ao processar login: {}", e.getMessage(), e);
            return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Erro ao processar login");
        }
    }

    @PostMapping("/login/enterprise")
    public ResponseEntity<?> loginEnterprise(@Valid @RequestBody AuthUserRequest authRequest) {
        logger.info("Tentativa de login empresarial para email: {}", authRequest.getEmail());
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            authRequest.getEmail(),
                            authRequest.getPassword()
                    )
            );
            UUID userId = userService.findUserIdByEmail(authRequest.getEmail());
            String token = jwtUtil.generateToken(authRequest.getEmail(), userId);
            AuthUserResponse response = AuthUserResponse.builder()
                    .token(token)
                    .userId(userId)
                    .build();
            logger.info("Login empresarial bem-sucedido para email: {}", authRequest.getEmail());
            return ResponseEntity.ok(response);
        } catch (BadCredentialsException e) {
            logger.warn("Credenciais inválidas para email: {}", authRequest.getEmail());
            return buildErrorResponse(HttpStatus.UNAUTHORIZED, "Credenciais inválidas");
        } catch (Exception e) {
            logger.error("Erro ao processar login empresarial: {}", e.getMessage(), e);
            return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Erro ao processar login");
        }
    }

    @PostMapping("/user/logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String authorizationHeader) {
        final String BEARER_PREFIX = "Bearer ";
        logger.info("Requisição de logout recebida");

        if (authorizationHeader == null || !authorizationHeader.startsWith(BEARER_PREFIX)) {
            logger.warn("Formato de cabeçalho Authorization inválido");
            return buildErrorResponse(HttpStatus.UNAUTHORIZED, "Cabeçalho Authorization inválido");
        }

        String token = authorizationHeader.substring(BEARER_PREFIX.length());
        try {
            if (tokenBlacklistService.isTokenBlacklisted(token)) {
                logger.warn("Token já está na lista negra");
                return buildErrorResponse(HttpStatus.BAD_REQUEST, "Token já invalidado");
            }

            long expirationInSeconds = jwtUtil.getExpirationTimeInSeconds(token);
            if (expirationInSeconds <= 0) {
                logger.warn("Token já expirado");
                return buildErrorResponse(HttpStatus.BAD_REQUEST, "Token já expirado");
            }

            tokenBlacklistService.addToBlacklist(token, expirationInSeconds);
            logger.info("Token adicionado à lista negra com sucesso. Expira em {} segundos", expirationInSeconds);
            return ResponseEntity.ok(buildSuccessResponse("Logout bem-sucedido"));
        } catch (TokenBlacklistService.RedisOperationException e) {
            logger.error("Falha na comunicação com Redis: {}", e.getMessage(), e);
            return buildErrorResponse(HttpStatus.SERVICE_UNAVAILABLE, "Serviço indisponível");
        } catch (JwtException | IllegalArgumentException e) {
            logger.error("Token JWT inválido: {}", e.getMessage(), e);
            return buildErrorResponse(HttpStatus.UNAUTHORIZED, "Token inválido");
        }
    }

    private ResponseEntity<Map<String, Object>> buildErrorResponse(HttpStatus status, String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("status", status.value());
        response.put("error", status.getReasonPhrase());
        response.put("message", message);
        return new ResponseEntity<>(response, status);
    }

    private Map<String, Object> buildSuccessResponse(String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("status", HttpStatus.OK.value());
        response.put("message", message);
        return response;
    }
}