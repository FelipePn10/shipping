package redirex.shipping.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AdminDTO {
    private Long id;
    private String name;
    private String email;
    private LocalDateTime createdAt;
}