package redirex.shipping.dto.response;

import java.time.LocalDateTime;

public record ApiResponse<T>(
        T data,
        ApiErrorResponse error,
        LocalDateTime timestamp
) {
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(data, null, LocalDateTime.now());
    }

    public static <T> ApiResponse<T> error(ApiErrorResponse error) {
        return new ApiResponse<>(null, error, LocalDateTime.now());
    }
}