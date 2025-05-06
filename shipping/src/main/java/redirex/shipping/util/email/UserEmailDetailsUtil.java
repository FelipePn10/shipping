package redirex.shipping.util.email;

public class UserEmailDetailsUtil {
    private String recipient;
    private String msgBody;
    private String subject;

    public UserEmailDetailsUtil() {
    }

    public UserEmailDetailsUtil(String recipient, String msgBody, String subject) {
        this.recipient = recipient;
        this.msgBody = msgBody;
        this.subject = subject;
    }

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public String getMsgBody() {
        return msgBody;
    }

    public void setMsgBody(String msgBody) {
        this.msgBody = msgBody;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    @Override
    public String toString() {
        return "UserEmailDetailsUtil{" +
                "recipient='" + recipient + '\'' +
                ", subject='" + subject + '\'' +
                ", msgBody='" + msgBody + '\'' +
                '}';
    }
}