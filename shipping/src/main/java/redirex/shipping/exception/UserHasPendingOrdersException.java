package redirex.shipping.exception;

public class UserHasPendingOrdersException extends RuntimeException {
    public UserHasPendingOrdersException(String message) {
        super(message);
    }
}