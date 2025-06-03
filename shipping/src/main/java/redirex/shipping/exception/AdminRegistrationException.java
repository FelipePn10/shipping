package redirex.shipping.exception;

public class AdminRegistrationException extends RuntimeException {
    public AdminRegistrationException(String message) {
        super(message);
    }

  public AdminRegistrationException(String message, Throwable cause) {
    super(message, cause);
  }
}
