package redirex.shipping.exception;

public class AddressDeleteException extends RuntimeException {
    public AddressDeleteException(String message) {
        super(message);
    }

    public AddressDeleteException(String message, Throwable cause) {
        super(message, cause);
    }
}
