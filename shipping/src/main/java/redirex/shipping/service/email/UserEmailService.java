package redirex.shipping.service.email;

import org.springframework.mail.MailException;


public interface UserEmailService {
    /**
     * Envia um código de verificação para redefinição de senha.
     *
     * @param to   Email do usuário
     * @param code Código de verificação (6 dígitos)
     */
    void sendPasswordResetCodeEmail(String to, String code);


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