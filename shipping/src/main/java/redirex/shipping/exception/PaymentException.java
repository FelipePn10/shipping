package redirex.shipping.exception;

// Exceção lançada para erros relacionados a pagamentos.
public class PaymentException extends RuntimeException {
    public PaymentException(String message) {
        super(message);
    }

    public PaymentException(String message, Throwable cause) {
        super(message, cause);
    }
}