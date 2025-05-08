package redirex.shipping.util.email;

public class EnterpriseEmailDetailsUtil {
    private String recipient;
    private String subject;
    private String msgBody;
    private String textContent;

    public EnterpriseEmailDetailsUtil() {
    }

    public EnterpriseEmailDetailsUtil(String recipient, String subject, String msgBody, String textContent) {
        this.recipient = recipient;
        this.subject = subject;
        this.msgBody = msgBody;
        this.textContent = textContent;
    }

    // Getters e Setters
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

    public String getMsgBody() {
        return msgBody;
    }

    public void setMsgBody(String msgBody) {
        this.msgBody = msgBody;
    }

    public String getTextContent() {
        return textContent;
    }

    public void setTextContent(String textContent) {
        this.textContent = textContent;
    }

    @Override
    public String toString() {
        return "EnterpriseEmailDetailsUtil{" +
                "recipient='" + recipient + '\'' +
                ", subject='" + subject + '\'' +
                ", msgBody='" + msgBody + '\'' +
                ", textContent='" + textContent + '\'' +
                '}';
    }

    private String abbreviate(String value, int maxLength) {
        if (value == null) return null;
        return value.length() > maxLength
                ? value.substring(0, maxLength) + "..."
                : value;
    }
}
