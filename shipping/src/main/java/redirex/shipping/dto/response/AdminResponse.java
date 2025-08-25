package redirex.shipping.dto.response;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
public class AdminResponse {
    private UUID id;
    private String name;
    private String email;
    private LocalDateTime createdAt;
    private List<OrderItemResponse> items;
}