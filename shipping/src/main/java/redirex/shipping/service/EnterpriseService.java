package redirex.shipping.service;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import redirex.shipping.dto.RegisterEnterpriseDTO;
import redirex.shipping.entity.EnterpriseEntity;
import redirex.shipping.repositories.EnterpriseRepository;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

@Service
@RequiredArgsConstructor
public class EnterpriseService {
    private static final Logger logger = (Logger) LoggerFactory.getLogger(EnterpriseService.class);

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
    public EnterpriseEntity register(@Valid RegisterEnterpriseDTO dto) {
        validateEnterpriseNotExists(dto.getEmail(), dto.getCnpj());

        try {
            EnterpriseEntity enterprise = new EnterpriseEntity(
                    dto.getName(),
                    dto.getEmail(),
                    passwordEncoder.encode(dto.getPassword()),
                    dto.getCnpj(),
                    dto.getPhone(),
                    dto.getAddress(),
                    dto.getComplement(),
                    dto.getCity(),
                    dto.getState(),
                    dto.getZipcode(),
                    dto.getCountry(),
                    dto.getOccupation(),
                    "ROLE_ENTERPRISE"
            );
            return enterpriseRepository.save(enterprise);
        } catch (Exception e) {
            logger.warning("Failed to register enterprise with email: {}");
            throw new EnterpriseRegistrationException("Failed to register enterprise", e);
        }
    }

    @Transactional
    public EnterpriseEntity registerNewEnterprise(@Valid RegisterEnterpriseDTO registerEnterpriseDTO) {
        EnterpriseEntity enterprise = new EnterpriseEntity();
        enterprise.setPassword(passwordEncoder.encode(registerEnterpriseDTO.getPassword()));
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
            logger.warning("Attempt to register duplicate email: {}");
            throw new EnterpriseRegistrationException("Email already registered");
        }

        Optional<EnterpriseEntity> existingUserByCnpj = enterpriseRepository.findByCnpj(cnpj);
        if (existingUserByCnpj.isPresent()) {
            logger.warning("Attempt to register duplicate CPF: {}");
            throw new EnterpriseRegistrationException("CPF already registered");
        }
    }
}
