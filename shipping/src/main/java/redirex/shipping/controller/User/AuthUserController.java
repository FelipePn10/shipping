package redirex.shipping.controller.User;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SecurityException;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;
import redirex.shipping.dto.request.AuthUserRequest;
import redirex.shipping.dto.response.ApiErrorResponse;
import redirex.shipping.dto.response.ApiResponse;
import redirex.shipping.dto.response.AuthUserResponse;
import redirex.shipping.security.JwtUtil;
import redirex.shipping.service.TokenBlacklistService;
import redirex.shipping.service.UserServiceImpl;

import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/public/auth/v1")
public class AuthUserController {
    private static final Logger logger = LoggerFactory.getLogger(AuthUserController.class);

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final TokenBlacklistService tokenBlacklistService;
    private final UserServiceImpl userService;

    public AuthUserController(
            AuthenticationManager authenticationManager,
            JwtUtil jwtUtil,
            TokenBlacklistService tokenBlacklistService,
            UserServiceImpl userService
    ) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.tokenBlacklistService = tokenBlacklistService;
        this.userService = userService;
    }

    @PostMapping("/user/login")
    public ResponseEntity<ApiResponse<AuthUserResponse>> login(
            @Valid @RequestBody AuthUserRequest authRequest) {

        logger.info("Tentativa de login para email: {}", authRequest.email());

        try {
            // Autentica o usuário - não precisa armazenar em variável, pois a exceção é lançada se falhar
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            authRequest.email(),
                            authRequest.password()
                    )
            );

            logger.debug("Autenticação bem-sucedida para: {}", authRequest.email());

            // Busca o ID do usuário
            UUID userId = userService.findUserIdByEmail(authRequest.email());

            // Gera o token JWT
            String token = jwtUtil.generateToken(authRequest.email(), userId);

            // Cria a resposta
            AuthUserResponse response = new AuthUserResponse(token, userId);

            logger.info("Login realizado com sucesso para: {}", authRequest.email());

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success(response));

        } catch (BadCredentialsException e) {
            logger.warn("Credenciais inválidas para: {}", authRequest.email());
            return buildErrorResponse(HttpStatus.UNAUTHORIZED, "Email ou senha incorretos");

        } catch (AuthenticationException e) {
            logger.warn("Falha na autenticação para {}: {}", authRequest.email(), e.getMessage());
            return buildErrorResponse(HttpStatus.UNAUTHORIZED, "Falha na autenticação");

        } catch (SecurityException e) {
            logger.error("Erro de segurança ao gerar token: {}", e.getMessage(), e);
            return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Erro interno de segurança");

        } catch (Exception e) {
            logger.error("Erro inesperado no login para {}: {}", authRequest.email(), e.getMessage(), e);
            return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Erro interno no processamento do login");
        }
    }

    @PostMapping("/user/logout")
    public ResponseEntity<ApiResponse<String>> logout(
            @RequestHeader(value = "Authorization", required = false) String authorizationHeader) {

        logger.info("Solicitação de logout recebida");

        // Valida o header Authorization
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            logger.warn("Header Authorization ausente ou malformado");
            return buildErrorResponse(HttpStatus.UNAUTHORIZED, "Header Authorization inválido");
        }

        String token = authorizationHeader.substring(7).trim(); // Remove "Bearer "

        if (token.isEmpty()) {
            logger.warn("Token vazio no header Authorization");
            return buildErrorResponse(HttpStatus.UNAUTHORIZED, "Token não fornecido");
        }

        try {
            // Verifica se o token já está na blacklist
            if (tokenBlacklistService.isTokenBlacklisted(token)) {
                logger.warn("Token já está revogado");
                return buildErrorResponse(HttpStatus.BAD_REQUEST, "Token já invalidado");
            }

            // Verifica se o token é válido antes de adicionar à blacklist
            String username = jwtUtil.getUsernameFromToken(token);

            // Calcula o tempo de expiração restante
            long expirationInSeconds = jwtUtil.getExpirationTimeInSeconds(token);

            if (expirationInSeconds <= 0) {
                logger.warn("Token já expirado: {}", username);
                return buildErrorResponse(HttpStatus.BAD_REQUEST, "Token já expirado");
            }

            // Adiciona à blacklist
            tokenBlacklistService.addToBlacklist(token, expirationInSeconds);

            logger.info("Logout realizado com sucesso para: {}. Token expira em {} segundos",
                    username, expirationInSeconds);

            return ResponseEntity.ok(ApiResponse.success("Logout realizado com sucesso"));

        } catch (ExpiredJwtException e) {
            logger.warn("Token expirado durante logout: {}", e.getClaims().getSubject());
            return buildErrorResponse(HttpStatus.BAD_REQUEST, "Token expirado");

        } catch (MalformedJwtException | SecurityException | IllegalArgumentException e) {
            logger.warn("Token JWT inválido durante logout: {}", e.getMessage());
            return buildErrorResponse(HttpStatus.UNAUTHORIZED, "Token inválido");

        } catch (TokenBlacklistService.RedisOperationException e) {
            logger.error("Falha na comunicação com Redis durante logout: {}", e.getMessage(), e);
            return buildErrorResponse(HttpStatus.SERVICE_UNAVAILABLE, "Serviço temporariamente indisponível");

        } catch (Exception e) {
            logger.error("Erro inesperado durante logout: {}", e.getMessage(), e);
            return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Erro interno durante logout");
        }
    }

    private <T> ResponseEntity<ApiResponse<T>> buildErrorResponse(HttpStatus status, String message) {
        ApiErrorResponse error = ApiErrorResponse.create(status, message);
        return ResponseEntity.status(status)
                .body(ApiResponse.error(error));
    }
}