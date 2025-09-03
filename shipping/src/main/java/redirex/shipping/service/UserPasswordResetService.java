package redirex.shipping.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import redirex.shipping.entity.PasswordResetToken;
import redirex.shipping.entity.UserEntity;
import redirex.shipping.repositories.PasswordResetTokenRepository;
import redirex.shipping.repositories.UserRepository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserPasswordResetService {
    private static final Logger logger = LoggerFactory.getLogger(UserPasswordResetService.class);

    private final UserRepository userRepository;
    private final PasswordResetTokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;

    public UserPasswordResetService(UserRepository userRepository,
                                    PasswordResetTokenRepository tokenRepository,
                                    PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // Gera o codigo de 6 dígitos e salva na tabela password_reset_tokens
    @Transactional
    @PreAuthorize("#user.email == authentication.name")
    public String createResetCode(UserEntity user) {
        String code = String.format("%06d", (int) (Math.random() * 999999));

        PasswordResetToken token = new PasswordResetToken();
        token.setUser(user);
        token.setCode(code);
        token.setExpiryDate(LocalDateTime.now().plusMinutes(10));
        token.setUsed(false);

        tokenRepository.save(token);
        logger.info("Generated password reset code for user: {}", user.getEmail());

        return code;
    }

    /** Valida o código e gera sessionToken */
    @Transactional
    @PreAuthorize("#user.email == authentication.name")
    public String verifyCode(UserEntity user, String code) {
        PasswordResetToken token = tokenRepository.findByUserAndCodeAndUsedFalse(user, code)
                .filter(t -> t.getExpiryDate().isAfter(LocalDateTime.now()))
                .orElseThrow(() -> new IllegalArgumentException("Invalid or expired code"));

        String sessionToken = UUID.randomUUID().toString();
        token.setSessionToken(sessionToken);
        token.setSessionExpiry(LocalDateTime.now().plusMinutes(15));
        tokenRepository.save(token);

        return sessionToken;
    }

    /** Reseta a senha com sessionToken */
    @Transactional
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public void resetPassword(String sessionToken, String newPassword) {
        PasswordResetToken token = tokenRepository.findBySessionTokenAndUsedFalse(sessionToken)
                .filter(t -> t.getSessionExpiry().isAfter(LocalDateTime.now()))
                .orElseThrow(() -> new IllegalArgumentException("Invalid or expired session"));

        UserEntity user = token.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        token.setUsed(true);
        tokenRepository.save(token);

        logger.info("Password reset successfully for user: {}", user.getEmail());
    }

    @Transactional(readOnly = true)
    public Optional<UserEntity> findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }
}
