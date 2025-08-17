package redirex.shipping.service.admin;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import redirex.shipping.dto.RegisterAdminDTO;
import redirex.shipping.dto.response.AdminResponse;
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
    public AdminResponse createAdmin(@Valid RegisterAdminDTO dto) {
        logger.info("Registering user with email: {}", dto.getEmail());
        validateAdminNotExists(dto.getEmail());

        try {
            AdminEntity admin = AdminEntity.builder()
                    .fullname(dto.getFullname())
                    .email(dto.getEmail())
                    .password(passwordEncoder.encode(dto.getPassword()))
                    .cpf(dto.getCpf())
                    .role("ROLE_ADMIN")
                    .build();
            logger.info("Creating admin with email: {}", dto.getEmail());

            adminRepository.save(admin);
            return adminMapper.toResponse(admin);

        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new AdminRegistrationException(e.getMessage());
        }
    }

    @Override
    @Transactional
    public AdminResponse updateAdmin(UUID id, @Valid RegisterAdminDTO dto) {
        logger.info("Updating user with email: {}", id);
        AdminEntity admin = adminRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Admin with ID " + id + " not found"));

        if (!dto.getEmail().equals(admin.getEmail())) {
            validateEmailNotExists(dto.getEmail());
        }

        admin.setFullname(dto.getFullname());
        if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
            admin.setPassword(passwordEncoder.encode(dto.getPassword()));
        }

        admin = adminRepository.save(admin);
        logger.info("Updating user with email: {}", admin.getEmail());
        return adminMapper.toResponse(admin);
    }

    private void validateAdminNotExists(String email) {
        validateEmailNotExists(email);
    }


    private void validateEmailNotExists(String email) {
        Optional<AdminEntity> existingUserByEmail = adminRepository.findByEmail(email);
        if (existingUserByEmail.isPresent()) {
            logger.warn("Attempt to use duplicate email: {}", email);
            throw new UserRegistrationException("Email already registered");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public UUID findAdminIdByEmail(String email) {
        logger.info("Finding admin with email: {}", email);
        return adminRepository.findByEmail(email)
                .map(AdminEntity::getId)
                .orElseThrow(() -> new ResourceNotFoundException("Admin with ID " + email + " not found"));
    }
}
