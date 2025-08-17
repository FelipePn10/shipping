package redirex.shipping.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class AdminDTO {
    private UUID id;
    private String name;
    private String email;
    private LocalDateTime createdAt;
}