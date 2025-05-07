package redirex.shipping.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import redirex.shipping.entity.UserEntity;
import redirex.shipping.repositories.UserRepository;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class UserPasswordResetService {

    private static final Logger logger = LoggerFactory.getLogger(UserPasswordResetService.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Value("${password.reset.token.timeout.minutes:15}")
    private long passwordResetTimeout;

    public void generateResetToken(UserEntity user) {
        logger.info("Generating password reset token for user: {}", user.getEmail());
        String token = UUID.randomUUID().toString();
        user.setPasswordResetToken(token);
        user.setPasswordResetTokenExpiry(LocalDateTime.now().plusMinutes(passwordResetTimeout));
        userRepository.save(user);
        logger.info("Password reset token generated and saved for user: {}", user.getEmail());
    }

    public void resetPassword(UserEntity user, String newPassword) {
        logger.info("Resetting password for user: {}", user.getEmail());
        user.setPassword(newPassword, passwordEncoder);
        user.setPasswordResetToken(null);
        user.setPasswordResetTokenExpiry(null);
        userRepository.save(user);
        logger.info("Password reset successfully for user: {}", user.getEmail());
    }
}