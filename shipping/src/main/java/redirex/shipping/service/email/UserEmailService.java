package redirex.shipping.service.email;

//Definindo o contrato para o envio de emails.
public interface UserEmailService {
    void sendPasswordResetEmail(String to, String token);
}
