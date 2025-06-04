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
import redirex.shipping.util.email.UserEmailDetailsUtil;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Year;

@Service
public class UserEmailServiceImpl implements UserEmailService {

    private static final Logger logger = LoggerFactory.getLogger(UserEmailServiceImpl.class);

    private final JavaMailSender javaMailSender;
    private final String mailFrom;
    private final String frontendUrl;
    private final String appName;

    @Autowired
    public UserEmailServiceImpl(
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
    public String sendSimpleMail(UserEmailDetailsUtil details) throws MailException {
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
            logger.error("Failed to send email to {}: {}", details.getRecipient(), e.getMessage());
            throw new MailException("Erro ao enviar email: " + e.getMessage(), e) {};
        }
    }

    @Override
    public void sendPasswordResetEmail(String to, String token) throws MailException {
        logger.info("Sending password reset email to: {}", to);

        try {
            String encodedToken = URLEncoder.encode(token, StandardCharsets.UTF_8.name());
            String cleanFrontendUrl = frontendUrl.endsWith("/") ? frontendUrl.substring(0, frontendUrl.length() - 1) : frontendUrl;
            String resetLink = cleanFrontendUrl + "/reset-password?email=" + URLEncoder.encode(to, StandardCharsets.UTF_8.name()) + "&token=" + encodedToken;

            UserEmailDetailsUtil details = new UserEmailDetailsUtil();
            details.setRecipient(to);
            details.setSubject("🔐 Redefinição de Senha - " + appName);
            details.setMsgBody(buildPasswordResetEmailHtml(resetLink, appName));
            details.setTextContent(buildPasswordResetEmailText(resetLink, appName));

            sendSimpleMail(details);
            logger.info("Password reset email sent successfully to: {}", to);
        } catch (UnsupportedEncodingException e) {
            logger.error("Encoding error while processing password reset email for {}: {}", to, e.getMessage());
            throw new MailException("Erro de codificação ao preparar o email de redefinição de senha", e) {};
        }
    }

    @Override
    public void sendWelcomeEmail(String to, String userName) throws MailException {
        logger.info("Sending welcome email to: {} with userName: {}", to, userName);

        UserEmailDetailsUtil details = new UserEmailDetailsUtil();
        details.setRecipient(to);
        details.setSubject("🚀 Bem-vindo(a) à bordo, " + userName + "! Sua jornada com " + appName + " começa agora!");

        String cleanFrontendUrl = frontendUrl.endsWith("/") ? frontendUrl.substring(0, frontendUrl.length() - 1) : frontendUrl;

        details.setMsgBody(buildWelcomeEmailHtml(userName, appName, cleanFrontendUrl));
        details.setTextContent(buildWelcomeEmailText(userName, appName, cleanFrontendUrl));

        sendSimpleMail(details);
        logger.info("Welcome email sent successfully to: {}", to);
    }

    private void prepareEmail(MimeMessageHelper helper, UserEmailDetailsUtil details)
            throws MessagingException, UnsupportedEncodingException {

        helper.setFrom(mailFrom, appName);
        helper.setTo(details.getRecipient());
        helper.setSubject(details.getSubject());

        if (details.getTextContent() != null && details.getMsgBody() != null) {
            helper.setText(details.getTextContent(), details.getMsgBody());
        } else if (details.getTextContent() != null) {
            helper.setText(details.getTextContent(), false);
        } else if (details.getMsgBody() != null) {
            helper.setText(details.getMsgBody(), true);
        } else {
            logger.error("Email content is empty for recipient: {}", details.getRecipient());
            throw new IllegalArgumentException("Email must have either text or HTML content, or both.");
        }
    }

    private String buildPasswordResetEmailHtml(String resetLink, String appName) {
        return new StringBuilder()
                .append("<!DOCTYPE html><html lang=\"pt-BR\"><head>")
                .append("<meta charset=\"UTF-8\"><meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">")
                .append("<title>Redefinição de Senha - ").append(appName).append("</title>")
                .append("<style>")
                // Reset e base styles
                .append("* { margin: 0; padding: 0; box-sizing: border-box; }")
                .append("body { font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif; background: #0a0a0a; color: #ffffff; line-height: 1.6; -webkit-font-smoothing: antialiased; -moz-osx-font-smoothing: grayscale; }")

                // Container principal
                .append(".email-container { max-width: 600px; margin: 0 auto; background: linear-gradient(145deg, #1a1a1a 0%, #2d2d2d 100%); border-radius: 20px; overflow: hidden; box-shadow: 0 25px 50px rgba(0,0,0,0.5), 0 0 0 1px rgba(255,255,255,0.05); }")

                // Header com gradiente
                .append(".header { background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); padding: 50px 40px; text-align: center; position: relative; overflow: hidden; }")
                .append(".header::before { content: ''; position: absolute; top: -50%; left: -50%; width: 200%; height: 200%; background: radial-gradient(circle, rgba(255,255,255,0.1) 0%, transparent 70%); animation: pulse 4s ease-in-out infinite; }")
                .append("@keyframes pulse { 0%, 100% { transform: scale(1); opacity: 0.5; } 50% { transform: scale(1.1); opacity: 0.8; } }")

                // Logo e título
                .append(".logo { width: 60px; height: 60px; background: linear-gradient(45deg, #ff6b6b, #ffd93d); border-radius: 15px; margin: 0 auto 20px; display: flex; align-items: center; justify-content: center; font-size: 24px; font-weight: bold; position: relative; z-index: 2; }")
                .append(".header h1 { color: #ffffff; font-size: 28px; font-weight: 700; margin: 0; position: relative; z-index: 2; text-shadow: 0 2px 4px rgba(0,0,0,0.3); }")
                .append(".header .subtitle { color: rgba(255,255,255,0.8); font-size: 16px; margin-top: 10px; position: relative; z-index: 2; }")

                // Conteúdo principal
                .append(".content { padding: 40px; }")
                .append(".security-alert { background: linear-gradient(135deg, #ff6b6b 0%, #ff8e8e 100%); padding: 20px; border-radius: 15px; margin-bottom: 30px; text-align: center; }")
                .append(".security-alert .icon { font-size: 48px; margin-bottom: 15px; }")
                .append(".security-alert h2 { color: #ffffff; font-size: 24px; margin-bottom: 10px; }")
                .append(".security-alert p { color: rgba(255,255,255,0.9); font-size: 16px; }")

                // Botão CTA
                .append(".cta-container { text-align: center; margin: 40px 0; }")
                .append(".cta-button { display: inline-block; background: linear-gradient(135deg, #4ecdc4 0%, #44a08d 100%); color: #ffffff !important; padding: 18px 40px; text-decoration: none; border-radius: 50px; font-size: 18px; font-weight: 600; text-transform: uppercase; letter-spacing: 1px; transition: all 0.3s ease; box-shadow: 0 10px 30px rgba(78, 205, 196, 0.3); }")
                .append(".cta-button:hover { transform: translateY(-3px); box-shadow: 0 15px 40px rgba(78, 205, 196, 0.4); }")

                // Informações de segurança
                .append(".security-info { background: rgba(255,255,255,0.05); padding: 25px; border-radius: 15px; margin: 30px 0; border-left: 4px solid #ffd93d; }")
                .append(".security-info h3 { color: #ffd93d; font-size: 18px; margin-bottom: 15px; }")
                .append(".security-info ul { list-style: none; padding: 0; }")
                .append(".security-info li { padding: 8px 0; color: rgba(255,255,255,0.8); position: relative; padding-left: 25px; }")
                .append(".security-info li::before { content: '🔒'; position: absolute; left: 0; }")

                // Footer
                .append(".footer { background: #0d0d0d; padding: 30px 40px; text-align: center; border-top: 1px solid rgba(255,255,255,0.1); }")
                .append(".footer p { color: rgba(255,255,255,0.6); font-size: 14px; margin-bottom: 10px; }")
                .append(".footer .app-name { color: #4ecdc4; font-weight: 600; }")

                // Responsivo
                .append("@media (max-width: 600px) {")
                .append("  .email-container { margin: 0; border-radius: 0; }")
                .append("  .header, .content, .footer { padding: 30px 20px; }")
                .append("  .header h1 { font-size: 24px; }")
                .append("  .cta-button { padding: 15px 30px; font-size: 16px; }")
                .append("}")
                .append("</style></head><body>")

                .append("<div class=\"email-container\">")
                .append("<div class=\"header\">")
                .append("<div class=\"logo\">🔐</div>")
                .append("<h1>Redefinição de Senha</h1>")
                .append("<p class=\"subtitle\">Vamos garantir a segurança da sua conta</p>")
                .append("</div>")

                .append("<div class=\"content\">")
                .append("<div class=\"security-alert\">")
                .append("<div class=\"icon\">⚡</div>")
                .append("<h2>Solicitação de Redefinição</h2>")
                .append("<p>Recebemos uma solicitação para redefinir a senha da sua conta</p>")
                .append("</div>")

                .append("<p style=\"color: rgba(255,255,255,0.8); font-size: 16px; margin-bottom: 30px;\">")
                .append("Clique no botão abaixo para criar uma nova senha segura. Este link é válido por apenas 30 minutos por questões de segurança.")
                .append("</p>")

                .append("<div class=\"cta-container\">")
                .append("<a href=\"").append(resetLink).append("\" class=\"cta-button\">Redefinir Senha</a>")
                .append("</div>")

                .append("<div class=\"security-info\">")
                .append("<h3>🛡️ Dicas de Segurança</h3>")
                .append("<ul>")
                .append("<li>Use uma senha forte com pelo menos 8 caracteres</li>")
                .append("<li>Combine letras maiúsculas, minúsculas, números e símbolos</li>")
                .append("<li>Nunca compartilhe sua senha com terceiros</li>")
                .append("<li>Se não foi você que solicitou, ignore este email</li>")
                .append("</ul>")
                .append("</div>")
                .append("</div>")

                .append("<div class=\"footer\">")
                .append("<p>&copy; ").append(Year.now()).append(" <span class=\"app-name\">").append(appName).append("</span>. Todos os direitos reservados.</p>")
                .append("<p>Este é um email automático. Por favor, não responda diretamente.</p>")
                .append("</div>")
                .append("</div>")
                .append("</body></html>")
                .toString();
    }

    private String buildPasswordResetEmailText(String resetLink, String appName) {
        return new StringBuilder()
                .append("🔐 REDEFINIÇÃO DE SENHA - ").append(appName).append("\n")
                .append("=".repeat(50)).append("\n\n")
                .append("⚡ SOLICITAÇÃO DE REDEFINIÇÃO\n\n")
                .append("Recebemos uma solicitação para redefinir a senha da sua conta.\n")
                .append("Clique no link abaixo para criar uma nova senha segura:\n\n")
                .append("🔗 ").append(resetLink).append("\n\n")
                .append("🛡️ INFORMAÇÕES DE SEGURANÇA:\n")
                .append("• Este link é válido por apenas 30 minutos\n")
                .append("• Use uma senha forte com pelo menos 8 caracteres\n")
                .append("• Combine letras maiúsculas, minúsculas, números e símbolos\n")
                .append("• Nunca compartilhe sua senha com terceiros\n")
                .append("• Se não foi você que solicitou, ignore este email\n\n")
                .append("Atenciosamente,\n")
                .append("Equipe ").append(appName).append("\n\n")
                .append("© ").append(Year.now()).append(" ").append(appName).append(". Todos os direitos reservados.")
                .toString();
    }

    private String buildWelcomeEmailHtml(String userName, String appName, String frontendUrl) {
        String dashboardUrl = frontendUrl + "/dashboard";
        String helpUrl = frontendUrl + "/ajuda";
        String supportUrl = frontendUrl + "/suporte";

        return new StringBuilder()
                .append("<!DOCTYPE html><html lang=\"pt-BR\"><head>")
                .append("<meta charset=\"UTF-8\"><meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">")
                .append("<title>Bem-vindo(a) ao ").append(appName).append("!</title>")
                .append("<style>")

                // Reset e base styles
                .append("* { margin: 0; padding: 0; box-sizing: border-box; }")
                .append("body { font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif; background: #0a0a0a; color: #ffffff; line-height: 1.6; -webkit-font-smoothing: antialiased; -moz-osx-font-smoothing: grayscale; }")

                // Container principal
                .append(".email-container { max-width: 650px; margin: 0 auto; background: linear-gradient(145deg, #1a1a1a 0%, #2d2d2d 100%); border-radius: 25px; overflow: hidden; box-shadow: 0 30px 60px rgba(0,0,0,0.6), 0 0 0 1px rgba(255,255,255,0.05); }")

                // Header épico
                .append(".header { background: linear-gradient(135deg, #667eea 0%, #764ba2 50%, #f093fb 100%); padding: 60px 40px; text-align: center; position: relative; overflow: hidden; }")
                .append(".header::before { content: ''; position: absolute; top: -50%; left: -50%; width: 200%; height: 200%; background: radial-gradient(circle, rgba(255,255,255,0.1) 0%, transparent 70%); animation: rotate 20s linear infinite; }")
                .append("@keyframes rotate { from { transform: rotate(0deg); } to { transform: rotate(360deg); } }")

                // Logo animado
                .append(".logo { width: 80px; height: 80px; background: linear-gradient(45deg, #ff6b6b, #ffd93d, #4ecdc4, #ff6b6b); background-size: 300% 300%; border-radius: 20px; margin: 0 auto 25px; display: flex; align-items: center; justify-content: center; font-size: 32px; position: relative; z-index: 2; animation: gradient 3s ease infinite; }")
                .append("@keyframes gradient { 0%, 100% { background-position: 0% 50%; } 50% { background-position: 100% 50%; } }")

                .append(".header h1 { color: #ffffff; font-size: 32px; font-weight: 800; margin: 0; position: relative; z-index: 2; text-shadow: 0 4px 8px rgba(0,0,0,0.3); }")
                .append(".header .tagline { color: rgba(255,255,255,0.9); font-size: 18px; margin-top: 15px; position: relative; z-index: 2; font-weight: 300; }")

                // Conteúdo principal
                .append(".content { padding: 50px 40px; }")

                // Mensagem de boas-vindas
                .append(".welcome-message { text-align: center; margin-bottom: 40px; }")
                .append(".welcome-message h2 { color: #ffffff; font-size: 28px; margin-bottom: 20px; }")
                .append(".welcome-message .highlight { background: linear-gradient(135deg, #4ecdc4, #44a08d); -webkit-background-clip: text; -webkit-text-fill-color: transparent; background-clip: text; font-weight: 700; }")
                .append(".welcome-message p { color: rgba(255,255,255,0.8); font-size: 18px; margin-bottom: 15px; }")

                // Botão CTA principal
                .append(".main-cta { text-align: center; margin: 50px 0; }")
                .append(".main-cta-button { display: inline-block; background: linear-gradient(135deg, #4ecdc4 0%, #44a08d 100%); color: #ffffff !important; padding: 20px 50px; text-decoration: none; border-radius: 50px; font-size: 20px; font-weight: 700; text-transform: uppercase; letter-spacing: 1.5px; transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1); box-shadow: 0 15px 35px rgba(78, 205, 196, 0.4); position: relative; overflow: hidden; }")
                .append(".main-cta-button::before { content: ''; position: absolute; top: 0; left: -100%; width: 100%; height: 100%; background: linear-gradient(90deg, transparent, rgba(255,255,255,0.2), transparent); transition: left 0.5s; }")
                .append(".main-cta-button:hover { transform: translateY(-5px); box-shadow: 0 20px 45px rgba(78, 205, 196, 0.5); }")
                .append(".main-cta-button:hover::before { left: 100%; }")

                // Cards de features
                .append(".features-grid { display: grid; grid-template-columns: repeat(auto-fit, minmax(250px, 1fr)); gap: 20px; margin: 50px 0; }")
                .append(".feature-card { background: rgba(255,255,255,0.05); padding: 30px; border-radius: 20px; text-align: center; transition: transform 0.3s ease; border: 1px solid rgba(255,255,255,0.1); }")
                .append(".feature-card:hover { transform: translateY(-10px); }")
                .append(".feature-card .icon { font-size: 48px; margin-bottom: 20px; }")
                .append(".feature-card h3 { color: #ffffff; font-size: 20px; margin-bottom: 15px; }")
                .append(".feature-card p { color: rgba(255,255,255,0.7); font-size: 14px; }")

                // Seção de estatísticas
                .append(".stats-section { background: linear-gradient(135deg, rgba(255,107,107,0.1) 0%, rgba(255,217,61,0.1) 100%); padding: 40px; border-radius: 20px; margin: 40px 0; text-align: center; }")
                .append(".stats-section h3 { color: #ffd93d; font-size: 24px; margin-bottom: 30px; }")
                .append(".stats-grid { display: grid; grid-template-columns: repeat(auto-fit, minmax(150px, 1fr)); gap: 30px; }")
                .append(".stat-item { }")
                .append(".stat-number { font-size: 32px; font-weight: 800; color: #4ecdc4; margin-bottom: 10px; }")
                .append(".stat-label { color: rgba(255,255,255,0.8); font-size: 14px; text-transform: uppercase; letter-spacing: 1px; }")

                // Links de ação
                .append(".action-links { display: flex; justify-content: center; gap: 30px; margin: 40px 0; flex-wrap: wrap; }")
                .append(".action-link { color: #ffffff; text-decoration: none; padding: 15px 25px; border-radius: 30px; font-weight: 600; transition: all 0.3s ease; }")
                .append(".action-link.support { background: linear-gradient(135deg, #ff6b6b, #ff8e8e); }")
                .append(".action-link.help { background: linear-gradient(135deg, #ffd93d, #ffed4e); color: #000000; }")
                .append(".action-link:hover { transform: translateY(-3px); box-shadow: 0 10px 25px rgba(0,0,0,0.3); }")

                // Footer
                .append(".footer { background: #0d0d0d; padding: 40px; text-align: center; border-top: 1px solid rgba(255,255,255,0.1); }")
                .append(".footer-links { display: flex; justify-content: center; gap: 30px; margin-bottom: 20px; flex-wrap: wrap; }")
                .append(".footer-links a { color: #4ecdc4; text-decoration: none; font-weight: 500; transition: color 0.3s ease; }")
                .append(".footer-links a:hover { color: #ffffff; }")
                .append(".footer p { color: rgba(255,255,255,0.6); font-size: 14px; margin-bottom: 10px; }")
                .append(".footer .app-name { color: #4ecdc4; font-weight: 700; }")

                // Responsivo
                .append("@media (max-width: 600px) {")
                .append("  .email-container { margin: 0; border-radius: 0; }")
                .append("  .header, .content, .footer { padding: 30px 20px; }")
                .append("  .header h1 { font-size: 26px; }")
                .append("  .welcome-message h2 { font-size: 24px; }")
                .append("  .main-cta-button { padding: 18px 35px; font-size: 18px; }")
                .append("  .features-grid { grid-template-columns: 1fr; }")
                .append("  .action-links { flex-direction: column; align-items: center; }")
                .append("  .footer-links { flex-direction: column; gap: 15px; }")
                .append("}")
                .append("</style></head><body>")

                .append("<div class=\"email-container\">")
                .append("<div class=\"header\">")
                .append("<div class=\"logo\">🚀</div>")
                .append("<h1>Bem-vindo(a) à bordo!</h1>")
                .append("<p class=\"tagline\">Sua jornada extraordinária começa agora</p>")
                .append("</div>")

                .append("<div class=\"content\">")
                .append("<div class=\"welcome-message\">")
                .append("<h2>Olá, <span class=\"highlight\">").append(userName).append("</span>!</h2>")
                .append("<p>🎉 É com imensa alegria que damos as boas-vindas à família <strong>").append(appName).append("</strong>!</p>")
                .append("<p>Prepare-se para uma experiência transformadora que vai revolucionar a forma como você gerencia seus envios.</p>")
                .append("</div>")

                .append("<div class=\"main-cta\">")
                .append("<a href=\"").append(dashboardUrl).append("\" class=\"main-cta-button\">Explorar Dashboard</a>")
                .append("</div>")

                .append("<div class=\"features-grid\">")
                .append("<div class=\"feature-card\">")
                .append("<div class=\"icon\">📦</div>")
                .append("<h3>Rastreamento Inteligente</h3>")
                .append("<p>Acompanhe todos os seus pacotes em tempo real com precisão milimétrica</p>")
                .append("</div>")
                .append("<div class=\"feature-card\">")
                .append("<div class=\"icon\">⚡</div>")
                .append("<h3>Automação Avançada</h3>")
                .append("<p>Deixe nossa IA cuidar dos processos repetitivos para você</p>")
                .append("</div>")
                .append("<div class=\"feature-card\">")
                .append("<div class=\"icon\">📊</div>")
                .append("<h3>Analytics Poderosos</h3>")
                .append("<p>Insights detalhados para otimizar suas operações logísticas</p>")
                .append("</div>")
                .append("</div>")

                .append("<div class=\"stats-section\">")
                .append("<h3>🌟 Junte-se a milhares de usuários satisfeitos</h3>")
                .append("<div class=\"stats-grid\">")
                .append("<div class=\"stat-item\">")
                .append("<div class=\"stat-number\">50K+</div>")
                .append("<div class=\"stat-label\">Usuários Ativos</div>")
                .append("</div>")
                .append("<div class=\"stat-item\">")
                .append("<div class=\"stat-number\">1M+</div>")
                .append("<div class=\"stat-label\">Pacotes Rastreados</div>")
                .append("</div>")
                .append("<div class=\"stat-item\">")
                .append("<div class=\"stat-number\">99.9%</div>")
                .append("<div class=\"stat-label\">Precisão</div>")
                .append("</div>")
                .append("</div>")
                .append("</div>")

                .append("<div class=\"action-links\">")
                .append("<a href=\"").append(supportUrl).append("\" class=\"action-link support\">💬 Suporte Premium</a>")
                .append("<a href=\"").append(helpUrl).append("\" class=\"action-link help\">📚 Central de Ajuda</a>")
                .append("</div>")

                .append("<p style=\"color: rgba(255,255,255,0.8); text-align: center; margin-top: 40px; font-size: 16px;\">")
                .append("Dúvidas? Estamos aqui para ajudar! Nossa equipe de suporte está disponível 24/7 para garantir que você tenha a melhor experiência possível.")
                .append("</p>")

                .append("<p style=\"color: rgba(255,255,255,0.9); text-align: center; margin-top: 30px; font-size: 18px; font-weight: 600;\">")
                .append("Vamos juntos revolucionar o mundo da logística! 🌍✨")
                .append("</p>")
                .append("</div>")

                .append("<div class=\"footer\">")
                .append("<div class=\"footer-links\">")
                .append("<a href=\"").append(frontendUrl).append("/termos\">Termos de Serviço</a>")
                .append("<a href=\"").append(frontendUrl).append("/privacidade\">Política de Privacidade</a>")
                .append("<a href=\"").append(frontendUrl).append("/sobre\">Sobre Nós</a>")
                .append("<a href=\"").append(frontendUrl).append("/blog\">Blog</a>")
                .append("</div>")
                .append("<p>&copy; ").append(Year.now()).append(" <span class=\"app-name\">").append(appName).append("</span>. Todos os direitos reservados.</p>")
                .append("<p>Você está recebendo este email porque se juntou à nossa plataforma.</p>")
                .append("<p>Feito com ❤️ pela equipe ").append(appName).append("</p>")
                .append("</div>")
                .append("</div>")
                .append("</body></html>")
                .toString();
    }

    private String buildWelcomeEmailText(String userName, String appName, String frontendUrl) {
        String dashboardUrl = frontendUrl + "/dashboard";
        String helpUrl = frontendUrl + "/ajuda";
        String supportUrl = frontendUrl + "/suporte";
        String termsUrl = frontendUrl + "/termos";
        String privacyUrl = frontendUrl + "/privacidade";

        return new StringBuilder()
                .append("🚀 BEM-VINDO(A) À BORDO - ").append(appName).append("\n")
                .append("=".repeat(50)).append("\n\n")
                .append("Olá, ").append(userName).append("! 🎉\n\n")
                .append("É com imensa alegria que damos as boas-vindas à família ").append(appName).append("!\n")
                .append("Prepare-se para uma experiência transformadora que vai revolucionar\n")
                .append("a forma como você gerencia seus envios.\n\n")

                .append("🌟 EXPLORE SEU DASHBOARD:\n")
                .append("➤ ").append(dashboardUrl).append("\n\n")

                .append("⚡ RECURSOS INCRÍVEIS QUE VOCÊ VAI AMAR:\n")
                .append("📦 Rastreamento Inteligente - Acompanhe todos os seus pacotes em tempo real\n")
                .append("⚡ Automação Avançada - Deixe nossa IA cuidar dos processos repetitivos\n")
                .append("📊 Analytics Poderosos - Insights detalhados para otimizar suas operações\n")
                .append("🛡️ Segurança Premium - Proteção de dados de nível empresarial\n\n")

                .append("📈 JUNTE-SE A MILHARES DE USUÁRIOS SATISFEITOS:\n")
                .append("• 50K+ Usuários Ativos\n")
                .append("• 1M+ Pacotes Rastreados\n")
                .append("• 99.9% de Precisão\n\n")

                .append("🆘 PRECISA DE AJUDA?\n")
                .append("💬 Suporte Premium: ").append(supportUrl).append("\n")
                .append("📚 Central de Ajuda: ").append(helpUrl).append("\n\n")

                .append("Nossa equipe está disponível 24/7 para garantir que você tenha\n")
                .append("a melhor experiência possível!\n\n")

                .append("Vamos juntos revolucionar o mundo da logística! 🌍✨\n\n")

                .append("Atenciosamente,\n")
                .append("Equipe ").append(appName).append(" ❤️\n\n")
                .append("---\n")
                .append("© ").append(Year.now()).append(" ").append(appName).append(". Todos os direitos reservados.\n")
                .append("Você está recebendo este email porque se juntou à nossa plataforma.\n\n")
                .append("🔗 Links Úteis:\n")
                .append("Termos de Serviço: ").append(termsUrl).append("\n")
                .append("Política de Privacidade: ").append(privacyUrl).append("\n")
                .toString();
    }

    private void validateEmailDetails(UserEmailDetailsUtil details) {
        if (details == null) {
            throw new IllegalArgumentException("Detalhes do e-mail não podem ser nulos");
        }
        if (details.getRecipient() == null || details.getRecipient().isBlank()) {
            throw new IllegalArgumentException("Destinatário não pode ser vazio");
        }
        if (details.getSubject() == null || details.getSubject().isBlank()) {
            throw new IllegalArgumentException("Assunto não pode ser vazio");
        }
        if ((details.getMsgBody() == null || details.getMsgBody().isBlank()) &&
                (details.getTextContent() == null || details.getTextContent().isBlank())) {
            throw new IllegalArgumentException("E-mail deve conter conteúdo HTML ou texto");
        }
    }
}