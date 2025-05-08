package redirex.shipping.controllers;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import redirex.shipping.dto.ForgotPasswordDTO;
import redirex.shipping.dto.LoginDTO;
import redirex.shipping.dto.RegisterUserDTO;
import redirex.shipping.dto.ResetPasswordDTO;
import redirex.shipping.entity.UserEntity;
import redirex.shipping.repositories.UserRepository;
import redirex.shipping.security.JwtUtil;
import redirex.shipping.service.TokenBlacklistService;
import redirex.shipping.service.UserPasswordResetService;
import redirex.shipping.service.UserService;
import redirex.shipping.service.email.UserEmailService;

import java.util.Optional;

@RestController
@RequestMapping("/public/auth")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    private final TokenBlacklistService tokenBlacklistService;

    public AuthController(
            AuthenticationManager authenticationManager,
            JwtUtil jwtUtil,
            TokenBlacklistService tokenBlacklistService) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.tokenBlacklistService = tokenBlacklistService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginDTO loginDTO) {
        logger.info("Login attempt for username: {}", loginDTO.getEmail());
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginDTO.getEmail(),
                            loginDTO.getPassword()
                    )
            );
            String token = jwtUtil.generateToken(loginDTO.getEmail()); // gera o token ao realizar o login
            return ResponseEntity.ok(new LoginResponse(token));
        } catch (BadCredentialsException e) {
            logger.warn("Invalid credentials for email: {}", loginDTO.getEmail());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponse("Credenciais inválidas"));
        } catch (Exception e) {
            logger.error("Error processing login: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse("Erro ao processar login"));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        final String BEARER_PREFIX = "Bearer ";
        final int BEARER_PREFIX_LENGTH = BEARER_PREFIX.length();

        logger.info("Logout request received");

        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith(BEARER_PREFIX)) {
            logger.warn("Invalid Authorization header format");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse("Cabeçalho de autorização inválido"));
        }

        String token = header.substring(BEARER_PREFIX_LENGTH);

        try {
            if (tokenBlacklistService.isTokenBlacklisted(token)) {
                logger.warn("Token já está na blacklist");
                return ResponseEntity.badRequest()
                        .body(new ErrorResponse("Token já invalidado"));
            }

            long expirationInSeconds = jwtUtil.getExpirationTimeInSeconds(token);

            if (expirationInSeconds <= 0) {
                logger.warn("Token já expirado");
                return ResponseEntity.badRequest()
                        .body(new ErrorResponse("Token já expirado"));
            }

            tokenBlacklistService.addToBlacklist(token, expirationInSeconds);
            logger.info("Token invalidado com sucesso. Expira em {} segundos", expirationInSeconds);

            return ResponseEntity.ok()
                    .body(new SuccessResponse("Logout realizado com sucesso"));

        } catch (TokenBlacklistService.RedisOperationException e) {
            logger.error("Falha na comunicação com Redis: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body(new ErrorResponse("Serviço indisponível"));

        } catch (JwtException | IllegalArgumentException e) {
            logger.error("Token JWT inválido: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse("Token inválido"));
        }
    }

    // Classes auxiliares para respostas
    private static class LoginResponse {
        public String token;

        public LoginResponse(String token) {
            this.token = token;
        }
    }

    private static class ErrorResponse {
        public String error;

        public ErrorResponse(String error) {
            this.error = error;
        }
    }

    private static class SuccessResponse {
        public String message;

        public SuccessResponse(String message) {
            this.message = message;
        }
    }
}