package redirex.shipping.exception;

// Exceção lançada quando um cupom é inválido.
public class InvalidCouponException extends RuntimeException {
    public InvalidCouponException(String message) {
        super(message);
    }

    public InvalidCouponException(String message, Throwable cause) {
        super(message, cause);
    }
}