package redirex.shipping.exception;

// Execeção lançada quando o saldo da carteira é insuficiente para uma transação.
public class InsufficientBalanceException extends RuntimeException {
    public InsufficientBalanceException(String message) {
        super(message);
    }

    public InsufficientBalanceException(String message, Throwable cause) {
        super(message, cause);
    }
}