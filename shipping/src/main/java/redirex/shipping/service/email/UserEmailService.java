package redirex.shipping.service.email;

import redirex.shipping.util.email.UserEmailDetailsUtil;


public interface UserEmailService {
    /**
     * Envia um email de redefinição de senha.
     *
     * @param to    Endereço de email do destinatário
     * @param token Token de redefinição de senha
     */
    void sendPasswordResetEmail(String to, String token);

    /**
     * Envia um email simples com os detalhes fornecidos.
     *
     * @param details Detalhes do email (destinatário, corpo, assunto)
     * @return Mensagem indicando o resultado do envio
     */
    String sendSimpleMail(UserEmailDetailsUtil details);
}