package redirex.shipping.service;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import redirex.shipping.dto.RegisterUserDTO;
import redirex.shipping.entity.UserEntity;
import redirex.shipping.repositories.UserRepository;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public static class UserRegistrationException extends RuntimeException {
        public UserRegistrationException(String message) {
            super(message);
        }

        public UserRegistrationException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    @Transactional
    public UserEntity register(@Valid RegisterUserDTO dto) {
        validateUserNotExists(dto.getEmail(), dto.getCpf());

        try {
            UserEntity user = new UserEntity(
                    dto.getFullname(),
                    dto.getEmail(),
                    passwordEncoder.encode(dto.getPassword()),
                    dto.getCpf(),
                    dto.getPhone(),
                    dto.getAddress(),
                    dto.getComplement(),
                    dto.getCity(),
                    dto.getState(),
                    dto.getZipcode(),
                    dto.getCountry(),
                    dto.getOccupation(),
                    "ROLE_USER"
            );
            return userRepository.save(user);
        } catch (Exception e) {
            logger.error("Failed to register user with email: {}", dto.getEmail(), e);
            throw new UserRegistrationException("Failed to register user", e);
        }
    }

    @Transactional
    public UserEntity registerNewUser(@Valid RegisterUserDTO registerUserDTO) {
        UserEntity user = new UserEntity();
        user.setPassword(passwordEncoder.encode(registerUserDTO.getPassword()));
        return userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public List<UserEntity> getAllUsers() {
        logger.info("Retrieving all users");
        return userRepository.findAll();
    }

    private void validateUserNotExists(String email, String cpf) {
        Optional<UserEntity> existingUserByEmail = userRepository.findByEmail(email);
        if (existingUserByEmail.isPresent()) {
            logger.warn("Attempt to register duplicate email: {}", email);
            throw new UserRegistrationException("Email already registered");
        }

        Optional<UserEntity> existingUserByCpf = userRepository.findByCpf(cpf);
        if (existingUserByCpf.isPresent()) {
            logger.warn("Attempt to register duplicate CPF: {}", cpf);
            throw new UserRegistrationException("CPF already registered");
        }
    }
}