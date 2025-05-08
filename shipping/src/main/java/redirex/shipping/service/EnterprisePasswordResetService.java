package redirex.shipping.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import redirex.shipping.entity.EnterpriseEntity;
import redirex.shipping.repositories.EnterpriseRepository;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class EnterprisePasswordResetService {
    private static final Logger logger = LoggerFactory.getLogger(EnterprisePasswordResetService.class);

    @Autowired
    private EnterpriseRepository enterpriseRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Value("${password.reset.token.timeout.minutes:15}")
    private Long passwordResetTimeout;

    public void generateResetToken(EnterpriseEntity enterprise) {
        logger.info("Generating password reset token for enterprise: {}", enterprise.getName());
        String token = UUID.randomUUID().toString();
        enterprise.setPasswordResetToken(token);
        enterprise.setPasswordResetTokenExpiry(LocalDateTime.now().plusMinutes(passwordResetTimeout));
        enterpriseRepository.save(enterprise);
        logger.info("Password reset token generated and saved for enterprise: {}", enterprise.getName());
    }

    public void resetPassword(EnterpriseEntity enterprise, String newPassword) {
        logger.info("Resetting password for enterprise: {}", enterprise.getName());
        enterprise.setPassword(newPassword, passwordEncoder);
        enterprise.setPasswordResetToken(null);
        enterprise.setPasswordResetTokenExpiry(null);
        enterpriseRepository.save(enterprise);
        logger.info("Password reset successfully for enterprise: {}", enterprise.getName());
    }
}
