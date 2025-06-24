package redirex.shipping.service.email;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import redirex.shipping.service.email.template.EmailTemplateService;
import redirex.shipping.util.email.UserEmailDetailsUtil;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Service
public class UserEmailServiceImpl implements UserEmailService {

    private static final Logger logger = LoggerFactory.getLogger(UserEmailServiceImpl.class);

    private final JavaMailSender javaMailSender;
    private final EmailTemplateService emailTemplateService;
    private final String mailFrom;
    private final String appName;
    private final String frontendUrl;

    @Autowired
    public UserEmailServiceImpl(
            JavaMailSender javaMailSender,
            EmailTemplateService emailTemplateService,
            @Value("${mail.from}") String mailFrom,
            @Value("${app.name:Redirex Shipping}") String appName,
            @Value("${app.frontend.url:http://localhost:3000}") String frontendUrl) {

        this.javaMailSender = javaMailSender;
        this.emailTemplateService = emailTemplateService;
        this.mailFrom = mailFrom;
        this.appName = appName;
        this.frontendUrl = frontendUrl.endsWith("/") ? frontendUrl.substring(0, frontendUrl.length() - 1) : frontendUrl;

        validateConfiguration();
    }

    private void validateConfiguration() {
        if (mailFrom == null || mailFrom.isBlank()) {
            throw new IllegalStateException("Mail 'from' address must be configured");
        }
    }

    @Override
    public String sendSimpleMail(UserEmailDetailsUtil details) throws MailException {
        logger.info("Sending email to: {}", details.getRecipient());
        validateEmailDetails(details);

        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, StandardCharsets.UTF_8.name());

            helper.setFrom(mailFrom, appName);
            helper.setTo(details.getRecipient());
            helper.setSubject(details.getSubject());
            helper.setText(details.getTextContent(), details.getMsgBody());

            javaMailSender.send(message);

            logger.info("Email sent successfully to: {}", details.getRecipient());
            return "Email enviado com sucesso";
        } catch (MessagingException | UnsupportedEncodingException e) {
            logger.error("Failed to send email to {}: {}", details.getRecipient(), e.getMessage(), e);
            throw new MailException("Erro ao enviar email: " + e.getMessage(), e) {};
        }
    }

    @Override
    public void sendPasswordResetEmail(String to, String token) throws MailException {
        logger.info("Preparing password reset email for: {}", to);
        try {
            String encodedToken = URLEncoder.encode(token, StandardCharsets.UTF_8.name());
            String encodedEmail = URLEncoder.encode(to, StandardCharsets.UTF_8.name());
            String resetLink = String.format("%s/reset-password?email=%s&token=%s", frontendUrl, encodedEmail, encodedToken);

            // Delega a construção do conteúdo para o serviço de template
            String htmlBody = emailTemplateService.buildPasswordResetEmailHtml(resetLink);
            String textBody = emailTemplateService.buildPasswordResetEmailText(resetLink);

            UserEmailDetailsUtil details = new UserEmailDetailsUtil();
            details.setRecipient(to);
            details.setSubject("🔐 Redefinição de Senha - " + appName);
            details.setMsgBody(htmlBody);
            details.setTextContent(textBody);

            sendSimpleMail(details);
            logger.info("Password reset email sent to: {}", to);
        } catch (UnsupportedEncodingException e) {
            logger.error("Encoding error for password reset email to {}: {}", to, e.getMessage(), e);
            throw new MailException("Erro de codificação ao preparar o email de redefinição de senha", e) {};
        }
    }

    @Override
    public void sendWelcomeEmail(String to, String userName) throws MailException {
        logger.info("Preparing welcome email for: {} with userName: {}", to, userName);

        // Delega a construção do conteúdo para o serviço de template
        String htmlBody = emailTemplateService.buildWelcomeEmailHtml(userName);
        String textBody = emailTemplateService.buildWelcomeEmailText(userName);

        UserEmailDetailsUtil details = new UserEmailDetailsUtil();
        details.setRecipient(to);
        details.setSubject("🚀 Bem-vindo(a) a bordo, " + userName + "!");
        details.setMsgBody(htmlBody);
        details.setTextContent(textBody);

        sendSimpleMail(details);
        logger.info("Welcome email sent to: {}", to);
    }

    private void validateEmailDetails(UserEmailDetailsUtil details) {
        if (details == null) throw new IllegalArgumentException("Detalhes do e-mail não podem ser nulos");
        if (details.getRecipient() == null || details.getRecipient().isBlank()) throw new IllegalArgumentException("Destinatário não pode ser vazio");
        if (details.getSubject() == null || details.getSubject().isBlank()) throw new IllegalArgumentException("Assunto não pode ser vazio");
        if ((details.getMsgBody() == null || details.getMsgBody().isBlank()) && (details.getTextContent() == null || details.getTextContent().isBlank())) {
            throw new IllegalArgumentException("E-mail deve conter conteúdo HTML ou texto");
        }
    }
}