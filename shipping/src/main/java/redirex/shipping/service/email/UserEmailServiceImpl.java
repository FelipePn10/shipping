package redirex.shipping.service.email;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import redirex.shipping.util.email.UserEmailDetailsUtil;

@Service
public class UserEmailServiceImpl implements UserEmailService {

    private static final Logger logger = LoggerFactory.getLogger(UserEmailServiceImpl.class);

    @Autowired
    private JavaMailSender javaMailSender;

    @Value("${mail.from}")
    private String mailFrom;

    @Value("${app.frontend.url:http://localhost:3000}")
    private String frontendUrl;

    @Override
    public String sendSimpleMail(UserEmailDetailsUtil details) {
        logger.info("Sending email to: {}", details.getRecipient());
        validateEmailDetails(details);
        try {
            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setFrom(mailFrom);
            mailMessage.setTo(details.getRecipient());
            mailMessage.setText(details.getMsgBody());
            mailMessage.setSubject(details.getSubject());

            javaMailSender.send(mailMessage);
            logger.info("Email sent successfully to: {}", details.getRecipient());
            return "Email enviado com sucesso";
        } catch (Exception e) {
            logger.error("Error sending email to {}: {}", details.getRecipient(), e.getMessage());
            throw new RuntimeException("Erro ao enviar email: " + e.getMessage());
        }
    }

    @Override
    public void sendPasswordResetEmail(String to, String token) {
        logger.info("Sending password reset email to: {}", to);
        UserEmailDetailsUtil details = new UserEmailDetailsUtil();
        details.setRecipient(to);
        details.setSubject("Redefinição de Senha");
        details.setMsgBody(
                "Você solicitou a redefinição de sua senha. Clique no link abaixo para redefinir:\n" +
                        frontendUrl + "/reset-password?email=" + to + "&token=" + token + "\n" +
                        "Este link expira em 30 minutos. Se você não solicitou isso, ignore este email."
        );
        sendSimpleMail(details);
    }

    private void validateEmailDetails(UserEmailDetailsUtil details) {
        if (details.getRecipient() == null || details.getRecipient().isEmpty()) {
            throw new IllegalArgumentException("Recipient cannot be null or empty");
        }
        if (details.getSubject() == null || details.getSubject().isEmpty()) {
            throw new IllegalArgumentException("Subject cannot be null or empty");
        }
        if (details.getMsgBody() == null || details.getMsgBody().isEmpty()) {
            throw new IllegalArgumentException("Message body cannot be null or empty");
        }
    }
}