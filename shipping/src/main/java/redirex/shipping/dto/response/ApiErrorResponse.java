package redirex.shipping.dto.response;

import org.springframework.http.HttpStatus;

public record ApiErrorResponse(
        String message,
        int status
) {
    public static ApiErrorResponse create(HttpStatus status, String message) {
        return new ApiErrorResponse(message, status.value());
    }
}