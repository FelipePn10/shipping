package redirex.shipping.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import redirex.shipping.entity.UserEntity;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class UserPasswordResetService {
    @Value("${password.reset.token.timeout.minutes}")
    private long passwordResetTimeout;

    public void generateResetToken(UserEntity user) {
        String token = UUID.randomUUID().toString();
        user.setPasswordResetToken(token);
        user.setPasswordResetTokenExpiry(LocalDateTime.now().plusMinutes(passwordResetTimeout));

        //no futuro eu possa salvar no banco de dados usando userrepository e enviar token por email.
    }
}
