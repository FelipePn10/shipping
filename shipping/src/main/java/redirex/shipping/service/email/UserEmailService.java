package redirex.shipping.service.email;

import org.springframework.mail.MailException;
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

    /**
     * Sends a welcome email to a new user.
     *
     * @param to The recipient's email address.
     * @param userName The user's name for personalization.
     * @throws MailException if there is an error sending the email.
     */
    void sendWelcomeEmail(String to, String userName) throws MailException;
}