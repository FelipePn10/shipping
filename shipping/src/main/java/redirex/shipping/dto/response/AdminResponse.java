package redirex.shipping.dto.response;

import lombok.Builder;
import lombok.Data;
import redirex.shipping.dto.OrderItemDTO;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class AdminResponse {
    private Long id;
    private String name;
    private String email;
    private LocalDateTime createdAt;
    private List<OrderItemDTO> items;
}