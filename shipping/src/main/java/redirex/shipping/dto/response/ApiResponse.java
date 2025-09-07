package redirex.shipping.dto.response;


import java.time.LocalDateTime;

public record ApiResponse<T> (
    T data,
    ApiErrorResponse error,
    LocalDateTime timestamp
) {

}
