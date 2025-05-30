package redirex.shipping.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class AdminResponse {
    private Long id;
    private String name;
    private String email;
    private String administratorLoginCode;
    private LocalDateTime createdAt;
}