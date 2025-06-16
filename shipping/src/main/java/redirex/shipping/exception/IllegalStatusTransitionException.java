package redirex.shipping.exception;

public class IllegalStatusTransitionException extends RuntimeException {
    public IllegalStatusTransitionException(String message) {
        super(message);
    }

  public IllegalStatusTransitionException(String message, Throwable cause) {
    super(message, cause);
  }
}
