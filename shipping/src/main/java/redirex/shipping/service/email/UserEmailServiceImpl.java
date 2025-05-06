package redirex.shipping.service.email;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import redirex.shipping.util.email.UserEmailDetailsUtil;

// Injeta o email sender configurado em SecurityConfig
// sendSimpleMail Metodo: Aceita EmailDetails e constrói um SimpleMailMessage.
// Configurando Propriedades do Mail: Define remetente, destinatário, assunto e corpo da mensagem.
// Enviando o Email: Utiliza javaMailSender.send para despachar o email.

@Service
public class UserEmailServiceImpl {
   @Autowired
    private JavaMailSender javaMailSender;

   public String sendSimpleMail(UserEmailDetailsUtil details) {
       try {
           SimpleMailMessage mailMessage = new SimpleMailMessage();
           mailMessage.setFrom("felipepanosso21@gmail.com");
           mailMessage.setTo(details.getRecipient());
           mailMessage.setText(details.getMsgBody());
           mailMessage.setSubject(details.getSubject());

           javaMailSender.send(mailMessage);
           return "Email enviado com sucesso";
       } catch (Exception e) {
           return "Erro ao enviar email: " + e.getMessage();
       }
   }
}
