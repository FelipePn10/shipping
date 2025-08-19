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
        logger.info("Registrando admin com email: {}", dto.getEmail());
        validateAdminNotExists(dto.getEmail());

        try {
            AdminEntity admin = AdminEntity.builder()
                    .fullname(dto.getFullname())
                    .email(dto.getEmail())
                    .password(passwordEncoder.encode(dto.getPassword()))
                    .cpf(dto.getCpf())
                    .role("ROLE_ADMIN")
                    .build();
            logger.info("Criando admin com email: {}", dto.getEmail());

            adminRepository.save(admin);
            return adminMapper.toResponse(admin);

        } catch (Exception e) {
            logger.error("Erro ao criar admin: {}", e.getMessage());
            throw new AdminRegistrationException(e.getMessage());
        }
    }

    @Override
    @Transactional
    public AdminResponse updateAdmin(UUID id, @Valid RegisterAdminDTO dto) {
        logger.info("Atualizando admin com ID: {}", id);
        AdminEntity admin = adminRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Admin com ID " + id + " não encontrado"));

        if (!dto.getEmail().equals(admin.getEmail())) {
            validateEmailNotExists(dto.getEmail());
        }

        admin.setFullname(dto.getFullname());
        if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
            admin.setPassword(passwordEncoder.encode(dto.getPassword()));
        }

        admin = adminRepository.save(admin);
        logger.info("Admin atualizado com email: {}", admin.getEmail());
        return adminMapper.toResponse(admin);
    }

    private void validateAdminNotExists(String email) {
        validateEmailNotExists(email);
    }

    private void validateEmailNotExists(String email) {
        Optional<AdminEntity> existingAdminByEmail = adminRepository.findByEmail(email);
        if (existingAdminByEmail.isPresent()) {
            logger.warn("Tentativa de usar email duplicado: {}", email);
            throw new UserRegistrationException("Email já registrado");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public UUID findAdminIdByEmail(String email) {
        logger.info("Buscando ID do admin com email: {}", email);
        return adminRepository.findByEmail(email)
                .map(AdminEntity::getId)
                .orElseThrow(() -> new ResourceNotFoundException("Admin com email " + email + " não encontrado"));
    }
}