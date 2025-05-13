package redirex.shipping.controller;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import redirex.shipping.dto.ForgotPasswordDTO;
import redirex.shipping.dto.RegisterUserDTO;
import redirex.shipping.dto.ResetPasswordDTO;
import redirex.shipping.entity.UserEntity;
import redirex.shipping.repositories.UserRepository;
import redirex.shipping.service.UserPasswordResetService;
import redirex.shipping.service.UserServiceImpl;
import redirex.shipping.service.email.UserEmailService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/public/user")
public class UserController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    private final UserRepository userRepository;
    private final UserEmailService emailService;
    private final UserPasswordResetService passwordResetService;
    private final UserServiceImpl userServiceImpl;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserController(
            UserRepository userRepository,
            UserEmailService emailService,
            UserPasswordResetService passwordResetService,
            UserServiceImpl userServiceImpl,
            PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.emailService = emailService;
        this.passwordResetService = passwordResetService;
        this.userServiceImpl = userServiceImpl;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody RegisterUserDTO registerUserDTO) {
        try {
            logger.info("Recebida requisição para criar usuário: {}", registerUserDTO.getEmail());
            UserEntity newUser = userServiceImpl.registerNewUser(registerUserDTO);
            logger.info("Usuário criado com sucesso: {}", newUser.getEmail());
            return new ResponseEntity<>(newUser, HttpStatus.CREATED);
        } catch (DataIntegrityViolationException e) {
            logger.error("Erro de integridade de dados: {}", e.getMessage(), e);
            return new ResponseEntity<>(new ErrorResponse("Erro: Email, CPF ou outro campo único já existe"), HttpStatus.CONFLICT);
        } catch (Exception e) {
            logger.error("Erro ao criar usuário: {}", e.getMessage(), e);
            return new ResponseEntity<>(new ErrorResponse("Erro ao criar usuário: " + e.getMessage()), HttpStatus.BAD_REQUEST);
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
        String resetToken = user.getPasswordResetToken();
        LocalDateTime tokenExpiry = user.getPasswordResetTokenExpiry();

        if (resetToken == null || tokenExpiry == null) {
            logger.warn("No valid reset token for email: {}", resetPasswordDTO.getEmail());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse("Token de redefinição inválido"));
        }

        if (!resetToken.equals(resetPasswordDTO.getToken())) {
            logger.warn("Invalid token for email: {}", resetPasswordDTO.getEmail());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse("Token de redefinição inválido"));
        }

        if (tokenExpiry.isBefore(LocalDateTime.now())) {
            logger.warn("Expired token for email: {}", resetPasswordDTO.getEmail());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse("Token de redefinição expirado"));
        }

        user.setPassword(passwordEncoder.encode(resetPasswordDTO.getNewPassword()));
        user.setPasswordResetToken(null);
        user.setPasswordResetTokenExpiry(null);
        userRepository.save(user);
        return ResponseEntity.ok(new SuccessResponse("Senha redefinida com sucesso"));
    }

    @GetMapping
    public ResponseEntity<List<UserEntity>> getAllUsers() {
        try {
            logger.info("Recebida requisição para listar todos os usuários");
            List<UserEntity> users = userServiceImpl.getAllUsers();
            logger.info("Retornando {} usuários", users.size());
            return new ResponseEntity<>(users, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Erro ao listar usuários: {}", e.getMessage(), e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Classes auxiliares para respostas
    static class ErrorResponse {
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