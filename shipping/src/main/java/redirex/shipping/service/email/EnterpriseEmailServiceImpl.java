package redirex.shipping.service.email;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.time.Year;

@Service
public class EnterpriseEmailServiceImpl implements EnterpriseEmailService {
    private static final Logger logger = LoggerFactory.getLogger(EnterpriseEmailServiceImpl.class);

    private final JavaMailSender javaMailSender;
    private final String mailFrom;
    private final String frontendUrl;
    private final String appName;

    @Autowired
    public EnterpriseEmailServiceImpl(
            JavaMailSender javaMailSender,
            @Value("${mail.from}") String mailFrom,
            @Value("${app.frontend.url:http://localhost:3000}") String frontendUrl,
            @Value("${app.name:Redirex Shipping}") String appName) {
        this.javaMailSender = javaMailSender;
        this.mailFrom = mailFrom;
        this.frontendUrl = frontendUrl;
        this.appName = appName;

        validateConfiguration();
    }

    private void validateConfiguration() {
        if (mailFrom == null || mailFrom.isBlank()) {
            throw new IllegalStateException("Mail 'from' address must be configured");
        }
        if (frontendUrl == null || frontendUrl.isBlank()) {
            throw new IllegalStateException("Frontend URL must be configured");
        }
    }

    @Override
    public String sendSimpleMail(EnterpriseEmailDetailsUtil details) throws MailException {
        logger.info("Sending email to: {}", details.getRecipient());
        validateEmailDetails(details);

        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, StandardCharsets.UTF_8.name());

            prepareEmail(helper, details);
            javaMailSender.send(message);

            logger.info("Email sent successfully to: {}", details.getRecipient());
            return "Email enviado com sucesso";

        } catch (MessagingException | UnsupportedEncodingException e) {
            logger.error("Falied to send email to {}: {}", details.getRecipient(), e.getMessage());
            throw new MailException("Erro ao enviar email") {
            };
        }
    }

    @Override
    public void sendPasswordResetEmail(String to, String token) throws MailException {
        logger.info("Sending password reset email to: {}", to);
        try {
            String encodedToken = java.net.URLEncoder.encode(token, StandardCharsets.UTF_8.name());
            String resetLink = frontendUrl + "/reset-password?email=" + to + "&token=" + encodedToken;

            // Correção: Usar UserEmailDetailsUtil em vez de EnterpriseEmailDetailsUtil
            EnterpriseEmailDetailsUtil details = new EnterpriseEmailDetailsUtil();
            details.setRecipient(to);
            details.setSubject("Redefinição de Senha - " + appName);
            details.setMsgBody(buildPasswordResetEmailHtml(resetLink));
            details.setTextContent(buildPasswordResetEmailText(resetLink));

            sendSimpleMail(details);
        } catch (UnsupportedEncodingException e) {
            logger.error("Encoding error while processing reset email: {}", e.getMessage());
            throw new MailException("Erro de codificação") {};
        }
    }

    private void prepareEmail(MimeMessageHelper helper, EnterpriseEmailDetailsUtil details)
            throws MessagingException, UnsupportedEncodingException {
        helper.setFrom(mailFrom, appName);
        helper.setTo(details.getRecipient());
        helper.setSubject(details.getSubject());

        if (details.getTextContent() != null && details.getMsgBody() != null) {
            helper.setText(details.getTextContent(), details.getMsgBody());
        } else if (details.getTextContent() != null) {
            helper.setText(details.getTextContent());
        } else if (details.getMsgBody() != null) {
            helper.setText(details.getMsgBody(), true);
        } else {
            throw new IllegalArgumentException("Email must have either text or HTML content");
        }
    }

    private String buildPasswordResetEmailHtml(String resetLink) {
        return new StringBuilder()
                .append("<!DOCTYPE html><html lang=\"pt-BR\"><head>")
                .append("<meta charset=\"UTF-8\"><meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">")
                .append("<title>Redefinição de Senha</title><style>")
                .append("body{font-family:Arial,sans-serif;margin:0;padding:20px;background:#f5f5f5;}")
                .append(".container{max-width:600px;margin:0 auto;background:#fff;border-radius:8px;padding:30px;}")
                .append(".button{display:inline-block;background:#007bff;color:#fff!important;padding:12px 24px;text-decoration:none;border-radius:4px;}")
                .append(".footer{margin-top:30px;text-align:center;color:#666;font-size:0.9em;}")
                .append("</style></head><body>")
                .append("<div class=\"container\">")
                .append("<h2 style=\"color:#333;margin-bottom:20px;\">Redefinição de Senha</h2>")
                .append("<p style=\"color:#555;line-height:1.6;\">Você solicitou a redefinição de senha. Clique no botão abaixo para continuar:</p>")
                .append("<p><a href=\"").append(resetLink).append("\" class=\"button\">Redefinir Senha</a></p>")
                .append("<p style=\"color:#888;font-size:0.9em;margin-top:30px;\">Este link expira em 30 minutos. Caso não tenha solicitado, ignore este e-mail.</p>")
                .append("<div class=\"footer\">")
                .append("<p>© ").append(Year.now()).append(" ").append(appName).append("</p>")
                .append("</div></div></body></html>")
                .toString();
    }

    private String buildPasswordResetEmailText(String resetLink) {
        return new StringBuilder()
                .append("Redefinição de Senha\n\n")
                .append("Clique no link abaixo para redefinir sua senha:\n")
                .append(resetLink).append("\n\n")
                .append("Este link expirará em 30 minutos.\n")
                .append("Caso não tenha solicitado esta alteração, ignore este e-mail.\n\n")
                .append("Atenciosamente,\n")
                .append(appName)
                .toString();
    }

    private void validateEmailDetails(EnterpriseEmailDetailsUtil details) {
        if (details.getRecipient() == null || details.getRecipient().isBlank()) {
            throw new IllegalArgumentException("Destinatário não pode ser vazio");
        }
        if (details.getSubject() == null || details.getSubject().isBlank()) {
            throw new IllegalArgumentException("Assunto não pode ser vazio");
        }
        if (details.getMsgBody() == null && details.getTextContent() == null) {
            throw new IllegalArgumentException("E-mail deve conter conteúdo HTML ou texto");
        }
    }
}