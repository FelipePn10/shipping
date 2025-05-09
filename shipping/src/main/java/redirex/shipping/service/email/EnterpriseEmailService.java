package redirex.shipping.service.email;

import redirex.shipping.util.email.EnterpriseEmailDetailsUtil;

public interface EnterpriseEmailService {

    /**
     * Envia um email de confirmação de cadastro de empresa.
     *
     * @param to    Endereço de email do destinatário
     * @param token Token de confirmação de cadastro
     */
    void sendPasswordResetEmail(String to, String token);

    /**
     * Envia um email de redefinição de senha.
     *
     * @param details Endereço de email do destinatário
     * @return Menssagem indicando o resultado do envio
     */
    String sendSimpleMail(EnterpriseEmailDetailsUtil details);
}
