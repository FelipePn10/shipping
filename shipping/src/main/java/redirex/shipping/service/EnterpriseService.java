package redirex.shipping.service;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import redirex.shipping.dto.request.RegisterEnterpriseRequest;
import redirex.shipping.entity.EnterpriseEntity;
import redirex.shipping.repositories.EnterpriseRepository;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EnterpriseService {
    private static final Logger logger = LoggerFactory.getLogger(EnterpriseService.class);

    private EnterpriseRepository enterpriseRepository;
    private PasswordEncoder passwordEncoder;

    public static class EnterpriseRegistrationException extends RuntimeException {
        public EnterpriseRegistrationException(String message) {
            super(message);
        }

        public EnterpriseRegistrationException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    @Transactional
    public EnterpriseEntity register(@Valid RegisterEnterpriseRequest dto) {
        validateEnterpriseNotExists(dto.email(), dto.cnpj());

        try {
            EnterpriseEntity enterprise = new EnterpriseEntity(
                    dto.name(),
                    dto.email(),
                    passwordEncoder.encode(dto.password()),
                    dto.cnpj(),
                    dto.phone(),
                    dto.address(),
                    dto.complement(),
                    dto.city(),
                    dto.state(),
                    dto.zipcode(),
                    dto.country(),
                    dto.occupation(),
                    "ROLE_ENTERPRISE"
            );
            return enterpriseRepository.save(enterprise);
        } catch (Exception e) {
            logger.error("Failed to register enterprise with email: {}");
            throw new EnterpriseRegistrationException("Failed to register enterprise", e);
        }
    }

    @Transactional
    public EnterpriseEntity registerNewEnterprise(@Valid RegisterEnterpriseRequest registerEnterpriseRequest) {
        EnterpriseEntity enterprise = new EnterpriseEntity();
        enterprise.setPassword(passwordEncoder.encode(registerEnterpriseRequest.password()));
        return enterpriseRepository.save(enterprise);
    }

    @Transactional(readOnly = true)
    public List<EnterpriseEntity> getAllEnterprises() {
        logger.info("Retrieving all enterprises");
        return enterpriseRepository.findAll();
    }

    private void validateEnterpriseNotExists(String email, String cnpj) {
        Optional<EnterpriseEntity> existingUserByEmail = enterpriseRepository.findByEmail(email);
        if (existingUserByEmail.isPresent()) {
            logger.warn("Attempt to register duplicate email: {}");
            throw new EnterpriseRegistrationException("Email already registered");
        }

        Optional<EnterpriseEntity> existingUserByCnpj = enterpriseRepository.findByCnpj(cnpj);
        if (existingUserByCnpj.isPresent()) {
            logger.warn("Attempt to register duplicate CPF: {}");
            throw new EnterpriseRegistrationException("CPF already registered");
        }
    }
}
