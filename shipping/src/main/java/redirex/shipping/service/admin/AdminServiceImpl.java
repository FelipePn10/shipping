package redirex.shipping.service.admin;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import redirex.shipping.dto.request.RegisterAdminRequest;
import redirex.shipping.dto.request.UpdateAdminRequest;
import redirex.shipping.dto.response.RegisterAdminResponse;
import redirex.shipping.dto.response.UpdateAdminResponse;
import redirex.shipping.entity.AdminEntity;
import redirex.shipping.exception.AdminRegistrationException;
import redirex.shipping.exception.ResourceNotFoundException;
import redirex.shipping.exception.UserRegistrationException;
import redirex.shipping.mapper.AdminMapper;
import redirex.shipping.repositories.AdminRepository;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {
    private static final Logger logger = LoggerFactory.getLogger(AdminServiceImpl.class);

    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;
    private final AdminMapper adminMapper;

    @Override
    @Transactional
    public RegisterAdminResponse createAdmin(@Valid RegisterAdminRequest registerAdminRequestDto) {
        logger.info("Registering admin with email: {}", registerAdminRequestDto.getEmail());
        validateAdminNotExists(registerAdminRequestDto.getEmail());

        try {
            AdminEntity adminEntity = AdminEntity.builder()
                    .fullname(registerAdminRequestDto.getFullname())
                    .email(registerAdminRequestDto.getEmail())
                    .password(passwordEncoder.encode(registerAdminRequestDto.getPassword()))
                    .cpf(registerAdminRequestDto.getCpf())
                    .role("ROLE_ADMIN")
                    .build();
            logger.info("Creating admin with email: {}", registerAdminRequestDto.getEmail());

            adminRepository.save(adminEntity);
            return adminMapper.toRegisterResponse(adminEntity);

        } catch (Exception e) {
            logger.error("Error creating admin: {}", e.getMessage());
            throw new AdminRegistrationException(e.getMessage());
        }
    }

    @Override
    @Transactional
    public UpdateAdminResponse updateAdmin(UUID id, @Valid UpdateAdminRequest updateAdminRequestDto) {
        logger.info("Updating adminEntity with ID: {}", id);
        AdminEntity adminEntity = adminRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Admin with id " + id + " not found"));

        if (!updateAdminRequestDto.getEmail().equals(adminEntity.getEmail())) {
            validateEmailNotExists(updateAdminRequestDto.getEmail());
        }

        adminEntity.setFullname(updateAdminRequestDto.getFullname());

        if (updateAdminRequestDto.getEmail() != null &&
                !updateAdminRequestDto.getEmail().isBlank() &&
                !updateAdminRequestDto.getEmail().equals(adminEntity.getEmail())) {
            validateEmailNotExists(updateAdminRequestDto.getEmail());
            adminEntity.setEmail(updateAdminRequestDto.getEmail());
        }

        if (updateAdminRequestDto.getPassword() != null &&
                !updateAdminRequestDto.getPassword().isBlank()) {
            adminEntity.setPassword(passwordEncoder.encode(updateAdminRequestDto.getPassword()));
        }

        adminEntity = adminRepository.save(adminEntity);
        logger.info("Admin updated with email: {}", adminEntity.getEmail());
        return adminMapper.toUpdateResponse(adminEntity);
    }

    private void validateAdminNotExists(String email) {
        validateEmailNotExists(email);
    }

    private void validateEmailNotExists(String email) {
        Optional<AdminEntity> existingAdminByEmail = adminRepository.findByEmail(email);
        if (existingAdminByEmail.isPresent()) {
            logger.warn("Attempt to use duplicate email: {}", email);
            throw new UserRegistrationException("Email already registered");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public UUID findAdminIdByEmail(String email) {
        logger.info("Fetching Admin ID with Email: {}", email);
        return adminRepository.findByEmail(email)
                .map(AdminEntity::getId)
                .orElseThrow(() -> new ResourceNotFoundException("Admin with email " + email + " not found"));
    }
}