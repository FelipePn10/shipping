package redirex.shipping.controller.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class AdminResponse {
    private Long id;
    private String name;
    private String email;
    private LocalDateTime createdAt;
}