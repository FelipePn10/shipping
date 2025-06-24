package redirex.shipping.service.email.template;

public interface EmailTemplateService {

    /**
     * @param resetLink O link para a página de redefinição de senha.
     * @return
     */
    String buildPasswordResetEmailHtml(String resetLink);

    /**
     * @param resetLink
     * @return
     */
    String buildPasswordResetEmailText(String resetLink);

    /**
     * @param userName O nome do novo usuário.
     * @return
     */
    String buildWelcomeEmailHtml(String userName);

    /**
     * @param userName
     * @return
     */
    String buildWelcomeEmailText(String userName);
}