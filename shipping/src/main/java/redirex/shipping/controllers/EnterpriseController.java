package redirex.shipping.controllers;


import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import redirex.shipping.dto.RegisterEnterpriseDTO;
import redirex.shipping.entity.EnterpriseEntity;
import redirex.shipping.repositories.EnterpriseRepository;
import redirex.shipping.service.EnterprisePasswordResetService;
import redirex.shipping.service.EnterpriseService;
import redirex.shipping.service.email.EnterpriseEmailService;

@RestController
@RequestMapping("/public/enterprise")
public class EnterpriseController {
    private static final Logger logger = LoggerFactory.getLogger(EnterpriseController.class);

    private final EnterpriseRepository enterpriseRepository;
    private final EnterpriseEmailService emailService;
    private final EnterprisePasswordResetService passwordResetService;
    private final EnterpriseService enterpriseService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public EnterpriseController(
            EnterpriseRepository enterpriseRepository,
            EnterpriseEmailService emailService,
            EnterprisePasswordResetService passwordResetService,
            EnterpriseService enterpriseService,
            PasswordEncoder passwordEncoder
    ) {
        this.enterpriseRepository = enterpriseRepository;
        this.emailService = emailService;
        this.passwordResetService = passwordResetService;
        this.enterpriseService = enterpriseService;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerEnterprise(@Valid @RequestBody RegisterEnterpriseDTO registerEnterpriseDTO) {
        try {
            logger.info("Recebida requisição para criar empresa: {}", registerEnterpriseDTO.getEmail());
            EnterpriseEntity newEnterprise = enterpriseService.registerNewEnterprise(registerEnterpriseDTO);
            logger.info("Empresa criada com sucesso: {}", newEnterprise.getEmail());
            return new ResponseEntity<>(newEnterprise, HttpStatus.CREATED);
        } catch (DataIntegrityViolationException e) {
            logger.error("Erro de integridade de dados: {}", e.getMessage(), e);
            return new ResponseEntity<>(new UserController.ErrorResponse("Erro: Email, CPF ou outro campo único já existe"), HttpStatus.CONFLICT);
        } catch (Exception e) {
            logger.error("Erro ao criar usuário: {}", e.getMessage(), e);
            return new ResponseEntity<>(new UserController.ErrorResponse("Erro ao criar usuário: " + e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }
}
