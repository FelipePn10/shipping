package redirex.shipping.util.email;

// Essa classe encapsula os detalhes essenciais necessários para enviar emails.
public class UserEmailDetailsUtil {
    private String recipient;
    private String msgBody;
    private String subject;

    // Getter e Setters
    public String getMsgBody() {
        return msgBody;
    }

    public void setMsgBody(String msgBody) {
        this.msgBody = msgBody;
    }

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }
}
