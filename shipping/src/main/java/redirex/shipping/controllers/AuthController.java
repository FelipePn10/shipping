package redirex.shipping.controllers;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import redirex.shipping.dto.ForgotPasswordDTO;
import redirex.shipping.dto.LoginDTO;
import redirex.shipping.dto.ResetPasswordDTO;
import redirex.shipping.entity.UserEntity;
import redirex.shipping.repositories.UserRepository;
import redirex.shipping.security.JwtUtil;
import redirex.shipping.service.UserPasswordResetService;
import redirex.shipping.service.email.UserEmailService;

import java.util.Optional;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final UserEmailService emailService;
    private final UserPasswordResetService passwordResetService;

    public AuthController(
            AuthenticationManager authenticationManager,
            JwtUtil jwtUtil,
            UserRepository userRepository,
            UserEmailService emailService,
            UserPasswordResetService passwordResetService) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
        this.emailService = emailService;
        this.passwordResetService = passwordResetService;
    }
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginDTO loginDTO) {
        logger.info("Login attempt for email: {}", loginDTO.getUsername());
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginDTO.getUsername(),
                            loginDTO.getPassword()
                    )
            );
            String token = jwtUtil.generateToken(loginDTO.getUsername());
            return ResponseEntity.ok(new LoginResponse(token));
        } catch (BadCredentialsException e) {
            logger.warn("Invalid credentials for email: {}", loginDTO.getUsername());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponse("Credenciais inválidas"));
        } catch (Exception e) {
            logger.error("Error processing login: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse("Erro ao processar login"));
        }
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@Valid @RequestBody ForgotPasswordDTO forgotPasswordDTO) {
        logger.info("Password reset request for email: {}", forgotPasswordDTO.getEmail());
        Optional<UserEntity> userOptional = userRepository.findByEmail(forgotPasswordDTO.getEmail());
        if (userOptional.isEmpty()) {
            logger.warn("No user found with email: {}", forgotPasswordDTO.getEmail());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse("Usuário não encontrado"));
        }

        UserEntity user = userOptional.get();
        passwordResetService.generateResetToken(user);
        userRepository.save(user);
        emailService.sendPasswordResetEmail(user.getEmail(), user.getPasswordResetToken());
        return ResponseEntity.ok(new SuccessResponse("Email de redefinição de senha enviado com sucesso"));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@Valid @RequestBody ResetPasswordDTO resetPasswordDTO) {
        logger.info("Password reset attempt for email: {}", resetPasswordDTO.getEmail());
        Optional<UserEntity> userOptional = userRepository.findByEmail(resetPasswordDTO.getEmail());
        if (userOptional.isEmpty()) {
            logger.warn("No user found with email: {}", resetPasswordDTO.getEmail());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse("Usuário não encontrado"));
        }

        UserEntity user = userOptional.get();
        if (user.getPasswordResetToken() == null || user.getPasswordResetTokenExpiry() == null) {
            logger.warn("No valid reset token for email: {}", resetPasswordDTO.getEmail());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse("Token de redefinição inválido"));
        }

        if (!user.getPasswordResetToken().equals(resetPasswordDTO.getToken())) {
            logger.warn("Invalid token for email: {}", resetPasswordDTO.getEmail());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse("Token de redefinição inválido"));
        }

        if (user.getPasswordResetTokenExpiry().isBefore(java.time.LocalDateTime.now())) {
            logger.warn("Expired token for email: {}", resetPasswordDTO.getEmail());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse("Token de redefinição expirado"));
        }

        user.setPassword(resetPasswordDTO.getNewPassword()); // Senha será codificada pelo serviço
        user.setPasswordResetToken(null);
        user.setPasswordResetTokenExpiry(null);
        userRepository.save(user);
        return ResponseEntity.ok(new SuccessResponse("Senha redefinida com sucesso"));
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