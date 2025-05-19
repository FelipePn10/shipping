package redirex.shipping.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CREATED)
public class AddressCreatedException extends RuntimeException {

    public AddressCreatedException(String message) {
        super(message);
    }

    public AddressCreatedException(String message, Throwable cause) {
        super(message, cause);
    }
}