package redirex.shipping.exception;

public class DepositWalletExecption extends RuntimeException {
    public DepositWalletExecption(String message) {
        super(message);
    }

    public DepositWalletExecption(String message, Throwable cause) {
        super(message, cause);
    }
}
