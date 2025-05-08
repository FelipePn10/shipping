package redirex.shipping.service.email;

public interface EnterpriseEmailService {

    /**
     * Envia um email de confirmação de cadastro de empresa.
     *
     * @param to    Endereço de email do destinatário
     * @param token Token de confirmação de cadastro
     */
    void sendRegistrationConfirmationEmail(String to, String token);

    /**
     * Envia um email de redefinição de senha.
     *
     * @param to    Endereço de email do destinatário
     * @param token Token de redefinição de senha
     */
    void sendPasswordResetEmail(String to, String token);
}
