package redirex.shipping.service;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import redirex.shipping.dto.RegisterAdminDTO;
import redirex.shipping.dto.response.AdminResponse;
import redirex.shipping.entity.AdminEntity;
import redirex.shipping.entity.UserEntity;
import redirex.shipping.exception.UserRegistrationException;
import redirex.shipping.repositories.AdminRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl {
    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    private final AdminRepository adminRepository;

    @Override
    @Transactional
    public AdminResponse createAdmin(@Valid RegisterAdminDTO dto) {
        logger.info("Registering user with email: {}", dto.getEmail());
        validateAdminNotExists(dto.getEmail(), dto.getAdministratorLoginCode());

        try {

        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }



    private void validateAdminNotExists(String email, String AdministratorLoginCode) {
        validateEmailNotExists(email);
        validateAdminLoginCode(AdministratorLoginCode);
    }


    private void validateEmailNotExists(String email) {
        Optional<AdminEntity> existingUserByEmail = adminRepository.findByEmail(email);
        if (existingUserByEmail.isPresent()) {
            logger.warn("Attempt to use duplicate email: {}", email);
            throw new UserRegistrationException("Email already registered");
        }
    }

    private void validateAdminLoginCode(String AdministratorLoginCode) {
        Optional<UserEntity> existsByAdministratorLoginCode = adminRepository.findByAdministratorLoginCode(AdministratorLoginCode);
        if (existsByAdministratorLoginCode.isPresent()) {
            logger.warn("Attempt to use duplicate AdministratorLoginCode: {}", AdministratorLoginCode);
            throw new UserRegistrationException("AdministratorLoginCode already registered");
        }
    }
}
