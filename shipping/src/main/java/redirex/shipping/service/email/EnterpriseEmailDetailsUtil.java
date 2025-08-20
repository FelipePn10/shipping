package redirex.shipping.service.email;

public class EnterpriseEmailDetailsUtil {
    private String recipient;
    private String subject;
    private String msgBody;
    private String textContent;

    public EnterpriseEmailDetailsUtil() {
        this.recipient = recipient;
        this.subject = subject;
        this.msgBody = msgBody;
        this.textContent = textContent;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
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

    public String getTextContent() {
        return textContent;
    }

    public void setTextContent(String textContent) {
        this.textContent = textContent;
    }

    public EnterpriseEmailDetailsUtil(String recipient, String subject, String msgBody) {
        this.recipient = recipient;
        this.subject = subject;
        this.msgBody = msgBody;
    }

    @Override
    public String toString() {
        return "EnterpriseEmailDetailsUtil{" +
                "recipient='" + recipient + '\'' +
                ", subject='" + subject + '\'' +
                ", msgBody='" + abbreviate(msgBody, 50) + '\'' +
                ", textContent='" + abbreviate(textContent, 50) + '\'' +
                '}';
    }

    private String abbreviate(String value, int maxLength) {
        if (value == null) return null;
        return value.length() > maxLength
                ? value.substring(0, maxLength) + "..."
                : value;
    }
}
