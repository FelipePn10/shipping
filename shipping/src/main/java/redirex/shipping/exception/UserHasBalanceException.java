package redirex.shipping.exception;

public class UserHasBalanceException extends RuntimeException {
    public UserHasBalanceException(String message) {
        super(message);
    }
}