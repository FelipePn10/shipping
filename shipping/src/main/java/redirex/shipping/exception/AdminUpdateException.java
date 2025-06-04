package redirex.shipping.exception;

public class AdminUpdateException extends RuntimeException {
    public AdminUpdateException(String message) {
        super(message);
    }

  public AdminUpdateException(String message, Throwable cause) {
    super(message, cause);
  }
}
