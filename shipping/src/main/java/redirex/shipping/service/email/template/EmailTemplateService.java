package redirex.shipping.service.email.template;

public interface EmailTemplateService {

    /**
     * @param code O código de verificação para redefinição de senha.
     * @return
     */
    String buildPasswordResetEmailHtml(String code);

    /**
     * @param code
     * @return
     */
    String buildPasswordResetEmailText(String code);

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