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
    public String buildPasswordResetEmailHtml(String code) {
        int currentYear = Year.now().getValue();
        return """
        <!DOCTYPE html>
        <html lang="pt-BR">
        <head>
            <meta charset="UTF-8">
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <title>Redefinição de Senha</title>
            <style>
                body { 
                    margin: 0; 
                    padding: 0; 
                    font-family: 'Segoe UI', 'Roboto', 'Helvetica Neue', sans-serif; 
                    background-color: #f8f9fa; 
                    color: #343a40; 
                    line-height: 1.6; 
                }
                .container { 
                    max-width: 600px; 
                    margin: 40px auto; 
                    background: #ffffff; 
                    border-radius: 4px; 
                    box-shadow: 0 2px 10px rgba(0,0,0,0.05); 
                    overflow: hidden; 
                    border-top: 4px solid #2c3e50;
                }
                .header { 
                    padding: 32px 40px 24px; 
                    text-align: center; 
                    border-bottom: 1px solid #eaeaea;
                }
                .header h1 { 
                    margin: 0; 
                    color: #2c3e50; 
                    font-size: 24px; 
                    font-weight: 500; 
                }
                .content { 
                    padding: 32px 40px; 
                }
                .content p { 
                    margin: 0 0 24px 0; 
                    font-size: 15px; 
                    color: #495057; 
                }
                .code-container { 
                    text-align: center; 
                    margin: 32px 0; 
                    background-color: #f1f3f5;
                    padding: 20px;
                    border-radius: 4px;
                }
                .code { 
                    font-size: 32px; 
                    font-weight: bold; 
                    color: #2c3e50; 
                    letter-spacing: 8px;
                }
                .alert { 
                    font-size: 14px; 
                    color: #868e96; 
                    background-color: #f8f9fa; 
                    padding: 16px; 
                    border-radius: 4px; 
                    border-left: 3px solid #ced4da; 
                    margin: 24px 0; 
                }
                .footer { 
                    padding: 24px 40px; 
                    text-align: center; 
                    font-size: 12px; 
                    color: #868e96; 
                    background-color: #f8f9fa; 
                    border-top: 1px solid #eaeaea; 
                }
                .footer a { 
                    color: #2c3e50; 
                    text-decoration: none; 
                }
            </style>
        </head>
        <body>
            <div class="container">
                <div class="header">
                    <h1>Redefinição de Senha</h1>
                </div>
                <div class="content">
                    <p>Prezado(a) usuário(a),</p>
                    
                    <p>Recebemos uma solicitação para redefinir a senha da sua conta em <strong>%s</strong>.</p>
                    
                    <p>Para prosseguir com a redefinição, utilize o código de verificação abaixo:</p>
                    
                    <div class="code-container">
                        <div class="code">%s</div>
                    </div>
                    
                    <div class="alert">
                        Este código será válido por 30 minutos. Caso não tenha solicitado esta alteração, 
                        ignore esta mensagem ou entre em contato com nosso suporte.
                    </div>
                </div>
                <div class="footer">
                    &copy; %d %s. Todos os direitos reservados.<br>
                    Esta é uma mensagem automática. Por favor não responda.
                </div>
            </div>
        </body>
        </html>
        """.formatted(appName, code, currentYear, appName);
    }

    @Override
    public String buildPasswordResetEmailText(String code) {
        return """
        REDEFINIÇÃO DE SENHA - %s
        ==============================================
        
        Prezado(a) usuário(a),
        
        Recebemos uma solicitação para redefinir a senha da sua conta.
        
        Seu código de verificação é: %s
        
        Este código é válido por 30 minutos.
        
        Caso não tenha solicitado esta alteração, ignore esta mensagem.
        
        Atenciosamente,
        Equipe %s
        """.formatted(appName, code, appName);
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
            <title>Bem-vindo(a)</title>
            <style>
                body { 
                    margin: 0; 
                    padding: 0; 
                    font-family: 'Segoe UI', 'Roboto', 'Helvetica Neue', sans-serif; 
                    background-color: #f8f9fa; 
                    color: #343a40; 
                    line-height: 1.6; 
                }
                .container { 
                    max-width: 600px; 
                    margin: 40px auto; 
                    background: #ffffff; 
                    border-radius: 4px; 
                    box-shadow: 0 2px 10px rgba(0,0,0,0.05); 
                    overflow: hidden; 
                    border-top: 4px solid #2c3e50;
                }
                .header { 
                    padding: 40px 40px 28px; 
                    text-align: center; 
                    border-bottom: 1px solid #eaeaea;
                }
                .header h1 { 
                    margin: 0; 
                    color: #2c3e50; 
                    font-size: 26px; 
                    font-weight: 500; 
                }
                .content { 
                    padding: 32px 40px; 
                }
                .content p { 
                    margin: 0 0 24px 0; 
                    font-size: 15px; 
                    color: #495057; 
                }
                .highlight {
                    font-weight: 500;
                    color: #2c3e50;
                }
                .button-container { 
                    text-align: center; 
                    margin: 32px 0 40px; 
                }
                .button { 
                    display: inline-block; 
                    padding: 12px 30px; 
                    color: #ffffff !important; 
                    background-color: #2c3e50; 
                    text-decoration: none; 
                    border-radius: 4px; 
                    font-size: 15px; 
                    font-weight: 500; 
                    transition: background-color 0.2s ease; 
                }
                .button:hover { 
                    background-color: #1a252f; 
                }
                .features { 
                    margin-top: 40px; 
                    border-top: 1px solid #eaeaea; 
                    padding-top: 32px; 
                }
                .feature { 
                    margin-bottom: 20px; 
                }
                .feature h3 { 
                    margin: 0 0 8px 0; 
                    font-size: 16px; 
                    font-weight: 500; 
                    color: #2c3e50; 
                }
                .feature p { 
                    margin: 0; 
                    font-size: 14px; 
                    color: #495057; 
                }
                .footer { 
                    padding: 24px 40px; 
                    text-align: center; 
                    font-size: 12px; 
                    color: #868e96; 
                    background-color: #f8f9fa; 
                    border-top: 1px solid #eaeaea; 
                }
                .footer a { 
                    color: #2c3e50; 
                    text-decoration: none; 
                }
            </style>
        </head>
        <body>
            <div class="container">
                <div class="header">
                    <h1>Bem-vindo(a) à <span class="highlight">%s</span></h1>
                </div>
                <div class="content">
                    <p>Prezado(a) <span class="highlight">%s</span>,</p>
                    
                    <p>Agradecemos por escolher a %s para gerenciar suas operações logísticas. Sua conta foi configurada com sucesso e está pronta para uso.</p>
                    
                    <p>Para começar, acesse seu painel de controle:</p>
                    
                    <div class="button-container">
                        <a href="%s" class="button">Acessar Painel</a>
                    </div>
                    
                    <div class="features">
                        <div class="feature">
                            <h3>Rastreamento Inteligente</h3>
                            <p>Monitoramento em tempo real de todas as suas remessas</p>
                        </div>
                        <div class="feature">
                            <h3>Gestão Eficiente</h3>
                            <p>Ferramentas integradas para otimizar sua operação</p>
                        </div>
                        <div class="feature">
                            <h3>Relatórios Analíticos</h3>
                            <p>Insights para tomada de decisão estratégica</p>
                        </div>
                    </div>
                    
                    <p>Estamos à disposição para qualquer dúvida ou sugestão através do nosso suporte.</p>
                </div>
                <div class="footer">
                    &copy; %d %s. Todos os direitos reservados.<br>
                    <a href="%s/suporte">Central de Ajuda</a> | <a href="%s/contato">Contato</a>
                </div>
            </div>
        </body>
        </html>
        """.formatted(appName, userName, appName, dashboardUrl, currentYear, appName, frontendUrl, frontendUrl);
    }

    @Override
    public String buildWelcomeEmailText(String userName) {
        String dashboardUrl = frontendUrl + "/dashboard";
        return """
        BEM-VINDO(A) À %s
        =================================================
        
        Prezado(a) %s,
        
        Agradecemos por escolher a %s para gerenciar suas operações logísticas.
        
        Sua conta foi configurada com sucesso e está pronta para uso:
        %s
        
        Recursos disponíveis:
        - Rastreamento Inteligente: Monitoramento em tempo real
        - Gestão Eficiente: Ferramentas integradas de operação
        - Relatórios Analíticos: Insights para tomada de decisão
        
        Estamos à disposição para qualquer dúvida ou sugestão.
        
        Atenciosamente,
        Equipe %s
        """.formatted(appName, userName, appName, dashboardUrl, appName);
    }
}