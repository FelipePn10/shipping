package redirex.shipping.service.email.template;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Year;

@Service
public class EmailTemplateServiceImpl implements EmailTemplateService {

    private final String frontendUrl;
    private final String appName;

    public EmailTemplateServiceImpl(
            @Value("${app.frontend.url:http://localhost:3000}") String frontendUrl,
            @Value("${app.name:Redirex Shipping}") String appName) {
        this.frontendUrl = frontendUrl.endsWith("/") ? frontendUrl.substring(0, frontendUrl.length() - 1) : frontendUrl;
        this.appName = appName;
    }

    @Override
    public String buildPasswordResetEmailHtml(String resetLink) {
        int currentYear = Year.now().getValue();
        return """
                <!DOCTYPE html>
                <html lang="pt-BR">
                <head>
                    <meta charset="UTF-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                    <title>Redefinição de Senha</title>
                    <style>
                        :root {
                            --bg-dark: #0a0a0a;
                            --bg-light: #1a1a1a;
                            --text-primary: #e0e0e0;
                            --text-secondary: #a0a0a0;
                            --accent-primary: #6a82fb;
                            --accent-secondary: #fc5c7d;
                        }
                        body { margin: 0; padding: 0; font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Helvetica, Arial, sans-serif; background-color: var(--bg-dark); color: var(--text-primary); }
                        .container { max-width: 600px; margin: 20px auto; background-color: var(--bg-light); border-radius: 16px; overflow: hidden; border: 1px solid #2a2a2a; }
                        .header { padding: 40px; text-align: center; background: linear-gradient(135deg, var(--accent-primary), var(--accent-secondary)); }
                        .header h1 { margin: 0; color: #ffffff; font-size: 28px; font-weight: 700; text-shadow: 0 2px 4px rgba(0,0,0,0.2); }
                        .content { padding: 30px 40px; }
                        .content p { font-size: 16px; line-height: 1.6; color: var(--text-primary); margin-bottom: 25px; }
                        .content .alert { font-size: 14px; color: var(--text-secondary); background-color: #2c2c2c; padding: 15px; border-radius: 8px; border-left: 4px solid var(--accent-primary); }
                        .button-container { text-align: center; margin: 30px 0; }
                        .button { display: inline-block; padding: 15px 35px; color: #ffffff !important; background: linear-gradient(135deg, var(--accent-primary), var(--accent-secondary)); text-decoration: none; border-radius: 50px; font-size: 16px; font-weight: 600; transition: transform 0.2s ease, box-shadow 0.2s ease; }
                        .button:hover { transform: translateY(-2px); box-shadow: 0 10px 20px rgba(0,0,0,0.2); }
                        .footer { padding: 30px; text-align: center; font-size: 12px; color: var(--text-secondary); border-top: 1px solid #2a2a2a; }
                        .footer a { color: var(--accent-primary); text-decoration: none; }
                    </style>
                </head>
                <body>
                    <div class="container">
                        <div class="header"><h1>🔐 Redefinição de Senha</h1></div>
                        <div class="content">
                            <p>Olá,</p>
                            <p>Recebemos uma solicitação para redefinir a senha da sua conta em <strong>%s</strong>. Se você não fez esta solicitação, pode ignorar este e-mail com segurança.</p>
                            <p>Para criar uma nova senha, clique no botão abaixo:</p>
                            <div class="button-container">
                                <a href="%s" class="button">Redefinir Minha Senha</a>
                            </div>
                            <p class="alert">Por segurança, este link expirará em 30 minutos.</p>
                            <p>Se tiver problemas com o botão, copie e cole o seguinte URL no seu navegador:<br><a href="%s" style="color: var(--text-secondary); word-break: break-all;">%s</a></p>
                        </div>
                        <div class="footer">
                            &copy; %d %s. Todos os direitos reservados.<br>
                            Este é um e-mail transacional.
                        </div>
                    </div>
                </body>
                </html>
                """.formatted(appName, resetLink, resetLink, resetLink, currentYear, appName);
    }

    @Override
    public String buildPasswordResetEmailText(String resetLink) {
        return """
                🔐 REDEFINIÇÃO DE SENHA - %s
                ==============================================
                Olá,

                Recebemos uma solicitação para redefinir a senha da sua conta.
                Se você não fez esta solicitação, pode ignorar este e-mail.

                Para criar uma nova senha, acesse o link abaixo:
                %s

                Este link é válido por 30 minutos.

                Atenciosamente,
                Equipe %s
                """.formatted(appName, resetLink, appName);
    }

    @Override
    public String buildWelcomeEmailHtml(String userName) {
        int currentYear = Year.now().getValue();
        String dashboardUrl = frontendUrl + "/dashboard";

        return """
                <!DOCTYPE html>
                <html lang="pt-BR">
                <head>
                    <meta charset="UTF-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                    <title>Bem-vindo(a) à %s!</title>
                    <style>
                        :root {
                            --bg-dark: #0f0c29; --bg-light: #1c1642; --text-primary: #ffffff; --text-secondary: #bdc3c7;
                            --accent-primary: #4e54c8; --accent-secondary: #8f94fb; --cta-bg: linear-gradient(135deg, #00c6ff, #0072ff);
                        }
                        body { margin: 0; padding: 0; font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Helvetica, Arial, sans-serif; background-color: var(--bg-dark); color: var(--text-primary); }
                        .container { max-width: 650px; margin: 20px auto; background-color: var(--bg-light); border-radius: 20px; overflow: hidden; border: 1px solid #2a2a3a; }
                        .header { padding: 60px 40px; text-align: center; background: linear-gradient(135deg, var(--accent-primary), #302b63, var(--bg-dark)); }
                        .header h1 { margin: 0; font-size: 32px; font-weight: 800; text-shadow: 0 3px 6px rgba(0,0,0,0.3); }
                        .header .highlight { background: -webkit-linear-gradient(45deg, #00c6ff, #c0c0ff); -webkit-background-clip: text; -webkit-text-fill-color: transparent; }
                        .content { padding: 40px 50px; }
                        .content p { font-size: 17px; line-height: 1.7; color: var(--text-secondary); margin-bottom: 25px; }
                        .button-container { text-align: center; margin: 40px 0; }
                        .button { display: inline-block; padding: 18px 45px; color: var(--text-primary) !important; background: var(--cta-bg); text-decoration: none; border-radius: 50px; font-size: 18px; font-weight: 700; transition: all 0.3s ease; box-shadow: 0 10px 25px rgba(0, 150, 255, 0.2); }
                        .button:hover { transform: translateY(-3px); box-shadow: 0 15px 30px rgba(0, 150, 255, 0.3); }
                        .features { margin-top: 50px; border-top: 1px solid #2a2a3a; padding-top: 40px; }
                        .feature { display: flex; align-items: center; margin-bottom: 25px; }
                        .feature-icon { font-size: 24px; margin-right: 20px; width: 40px; text-align: center; }
                        .feature-text h3 { margin: 0 0 5px 0; font-size: 18px; color: var(--text-primary); }
                        .feature-text p { margin: 0; font-size: 15px; color: var(--text-secondary); }
                        .footer { padding: 40px; text-align: center; font-size: 13px; color: #7f8c8d; background-color: #0a081e; }
                        .footer a { color: var(--accent-secondary); text-decoration: none; }
                    </style>
                </head>
                <body>
                    <div class="container">
                        <div class="header">
                            <h1>Bem-vindo(a), <span class="highlight">%s</span>!</h1>
                        </div>
                        <div class="content">
                            <p>É uma grande alegria ter você na família <strong>%s</strong>! Estamos empolgados para ajudar você a revolucionar a maneira como gerencia suas remessas.</p>
                            <p>Sua jornada para uma logística mais inteligente e eficiente começa agora. Explore a plataforma e descubra tudo o que preparamos para você.</p>
                            <div class="button-container">
                                <a href="%s" class="button">Acessar meu Dashboard</a>
                            </div>
                            <div class="features">
                                <div class="feature">
                                    <div class="feature-icon">📦</div>
                                    <div class="feature-text">
                                        <h3>Rastreamento Inteligente</h3>
                                        <p>Visibilidade total de suas remessas, em tempo real e com precisão.</p>
                                    </div>
                                </div>
                                <div class="feature">
                                    <div class="feature-icon">⚡</div>
                                    <div class="feature-text">
                                        <h3>Automação Avançada</h3>
                                        <p>Economize tempo e reduza erros com nossos fluxos de trabalho automatizados.</p>
                                    </div>
                                </div>
                                <div class="feature">
                                    <div class="feature-icon">📊</div>
                                    <div class="feature-text">
                                        <h3>Analytics Poderosos</h3>
                                        <p>Tome decisões baseadas em dados com relatórios e insights detalhados.</p>
                                    </div>
                                </div>
                            </div>
                        </div>
                        <div class="footer">
                            &copy; %d %s. Todos os direitos reservados.<br>
                            Se precisar de ajuda, <a href="%s/suporte">visite nossa central de suporte</a>.
                        </div>
                    </div>
                </body>
                </html>
                """.formatted(appName, userName, appName, dashboardUrl, currentYear, appName, frontendUrl);
    }

    @Override
    public String buildWelcomeEmailText(String userName) {
        String dashboardUrl = frontendUrl + "/dashboard";
        return """
                🚀 BEM-VINDO(A) À %s, %s!
                =================================================
                É uma grande alegria ter você conosco!
                
                Sua jornada para uma logística mais inteligente e eficiente começa agora.
                
                Acesse seu dashboard para começar:
                %s
                
                O que você pode fazer com a %s:
                
                📦 Rastreamento Inteligente: Visibilidade total de suas remessas.
                ⚡ Automação Avançada: Economize tempo e reduza erros.
                📊 Analytics Poderosos: Tome decisões baseadas em dados.
                
                Estamos aqui para ajudar! Se tiver qualquer dúvida, entre em contato com nosso suporte.
                
                Atenciosamente,
                Equipe %s ❤️
                """.formatted(appName, userName, dashboardUrl, appName, appName);
    }
}