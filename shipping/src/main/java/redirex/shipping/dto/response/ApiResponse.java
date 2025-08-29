package redirex.shipping.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ApiResponse<T> {
    private T data;
    private ApiErrorResponse error;
    private LocalDateTime timestamp;
}
