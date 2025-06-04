package redirex.shipping.exception;

public class SendeEmailWelcomeException extends RuntimeException {
    public SendeEmailWelcomeException(String message) {
        super(message);
    }

    public SendeEmailWelcomeException(String message, Throwable cause) {
        super(message, cause);
    }
}
