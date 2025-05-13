package redirex.shipping.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import redirex.shipping.entity.UserEntity;
import redirex.shipping.repositories.UserRepository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserPasswordResetService {
    private static final Logger logger = LoggerFactory.getLogger(UserPasswordResetService.class);

    private final UserRepository userRepository;

    @Transactional
    public void generateResetToken(UserEntity user) {
        String token = UUID.randomUUID().toString();
        user.setPasswordResetToken(token);
        user.setPasswordResetTokenExpiry(LocalDateTime.now().plusHours(24));
        userRepository.save(user);
        logger.info("Generated password reset token for user: {}", user.getEmail());
    }

    @Transactional(readOnly = true)
    public Optional<UserEntity> findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Transactional
    public void saveUser(UserEntity user) {
        userRepository.save(user);
    }
}