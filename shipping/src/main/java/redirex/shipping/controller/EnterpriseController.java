//package redirex.shipping.controller;
//
//
//import jakarta.validation.Valid;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.dao.DataIntegrityViolationException;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.web.bind.annotation.*;
//import redirex.shipping.dto.ForgotPasswordDTO;
//import redirex.shipping.dto.RegisterEnterpriseDTO;
//import redirex.shipping.dto.ResetPasswordDTO;
//import redirex.shipping.entity.EnterpriseEntity;
//import redirex.shipping.repositories.EnterpriseRepository;
//import redirex.shipping.service.EnterprisePasswordResetService;
//import redirex.shipping.service.EnterpriseService;
//import redirex.shipping.service.email.EnterpriseEmailService;
//
//import java.time.LocalDateTime;
//import java.util.Optional;
//
//@RestController
//@RequestMapping("/public/enterprise")
//public class EnterpriseController {
//    private static final Logger logger = LoggerFactory.getLogger(EnterpriseController.class);
//
//    private final EnterpriseRepository enterpriseRepository;
//    private final EnterpriseEmailService enterpriseEmailService;
//    private final EnterprisePasswordResetService passwordResetService;
//    private final EnterpriseService enterpriseService;
//    private final PasswordEncoder passwordEncoder;
//
//    @Autowired
//    public EnterpriseController(
//            EnterpriseRepository enterpriseRepository,
//            EnterpriseEmailService enterpriseEmailService,
//            EnterprisePasswordResetService passwordResetService,
//            EnterpriseService enterpriseService,
//            PasswordEncoder passwordEncoder
//    ) {
//        this.enterpriseRepository = enterpriseRepository;
//        this.enterpriseEmailService = enterpriseEmailService;
//        this.enterpriseService = enterpriseService;
//        this.passwordResetService = passwordResetService;
//        this.passwordEncoder = passwordEncoder;
//    }
//
//    @PostMapping("/register")
//    public ResponseEntity<?> registerEnterprise(@Valid @RequestBody RegisterEnterpriseDTO registerEnterpriseDTO) {
//        try {
//            logger.info("Recebida requisição para criar empresa: {}", registerEnterpriseDTO.getEmail());
//            EnterpriseEntity newEnterprise = enterpriseService.registerNewEnterprise(registerEnterpriseDTO);
//            logger.info("Empresa criada com sucesso: {}", newEnterprise.getEmail());
//            return new ResponseEntity<>(newEnterprise, HttpStatus.CREATED);
//        } catch (DataIntegrityViolationException e) {
//            logger.error("Erro de integridade de dados: {}", e.getMessage(), e);
//            return new ResponseEntity<>(new UserController.ErrorResponse("Erro: Email, CPF ou outro campo único já existe"), HttpStatus.CONFLICT);
//        } catch (Exception e) {
//            logger.error("Erro ao criar usuário: {}", e.getMessage(), e);
//            return new ResponseEntity<>(new UserController.ErrorResponse("Erro ao criar usuário: " + e.getMessage()), HttpStatus.BAD_REQUEST);
//        }
//    }
//
//    @PostMapping("/forgot-password")
//    public ResponseEntity<?> forgotPassword(@Valid @RequestBody ForgotPasswordDTO forgotPasswordDTO) {
//        logger.info("Password reset request for email: {}", forgotPasswordDTO.getEmail());
//        Optional<EnterpriseEntity> enterpriseOptional = enterpriseRepository.findByEmail(forgotPasswordDTO.getEmail());
//        if (enterpriseOptional.isEmpty()) {
//            logger.warn("No user found with email: {}", forgotPasswordDTO.getEmail());
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse("Empresa não encontrada"));
//        }
//
//        EnterpriseEntity enterprise = enterpriseOptional.get();
//        passwordResetService.generateResetToken(enterprise);
//        enterpriseRepository.save(enterprise);
//        enterpriseEmailService.sendPasswordResetEmail(enterprise.getEmail(), enterprise.getPasswordResetToken());
//        return ResponseEntity.ok(new SuccessResponse("Email de redefinição de senha enviado com sucesso"));
//    }
//
//    @PostMapping("/reset-password")
//    public ResponseEntity<?> resetPassword(@Valid @RequestBody ResetPasswordDTO resetPasswordDTO) {
//        logger.info("Password reset attemp for email: {}", resetPasswordDTO.getEmail());
//        Optional<EnterpriseEntity> enterpriseOptional = enterpriseRepository.findByEmail(resetPasswordDTO.getEmail());
//        if (enterpriseOptional.isEmpty()) {
//            logger.warn("No user found with email: {}", resetPasswordDTO.getEmail());
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse("Empresa não encontrada"));
//        }
//         EnterpriseEntity enterprise = enterpriseOptional.get();
//         String resetToken = enterprise.getPasswordResetToken();
//         LocalDateTime tokenExpiry = enterprise.getPasswordResetTokenExpiry();
//
//        if (resetToken == null || tokenExpiry == null) {
//            logger.warn("No valid reset token for email: {}", resetPasswordDTO.getEmail());
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new UserController.ErrorResponse("Token de redefinição inválido"));
//        }
//
//        if (!resetToken.equals(resetPasswordDTO.getToken())) {
//            logger.warn("Invalid token for email: {}", resetPasswordDTO.getEmail());
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new UserController.ErrorResponse("Token de redefinição inválido"));
//        }
//
//        if (tokenExpiry.isBefore(LocalDateTime.now())) {
//            logger.warn("Expired token for email: {}", resetPasswordDTO.getEmail());
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new UserController.ErrorResponse("Token de redefinição expirado"));
//        }
//
//        enterprise.setPassword(passwordEncoder.encode(resetPasswordDTO.getNewPassword()));
//        enterprise.setPasswordResetToken(null);
//        enterprise.setPasswordResetTokenExpiry(null);
//        enterpriseRepository.save(enterprise);
//        return ResponseEntity.ok(new SuccessResponse("Senha redefinida com sucesso"));
//    }
//
//    @GetMapping
//    public ResponseEntity<ErrorResponse> getAllEnterprises() {
//        try {
//            logger.info("Recebida requisição para listar todas as empresas");
//            return ResponseEntity.ok((ErrorResponse) enterpriseRepository.findAll());
//        } catch (Exception e) {
//            logger.error("Erro ao listar todas as empresas: {}", e.getMessage(), e);
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                    .body(new ErrorResponse("Erro ao listar todas as empresas: " + e.getMessage()));
//        }
//    }
//
//
//    // Classes auxiliares para respostas
//    static class ErrorResponse {
//        public String error;
//
//        public ErrorResponse(String error) {
//            this.error = error;
//        }
//    }
//
//    private static class SuccessResponse {
//        public String message;
//
//        public SuccessResponse(String message) {
//            this.message = message;
//        }
//    }
//}
