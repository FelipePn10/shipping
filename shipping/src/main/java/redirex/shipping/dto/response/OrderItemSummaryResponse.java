package redirex.shipping.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import redirex.shipping.enums.OrderItemStatusEnum;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
public class OrderItemSummaryResponse {
    private UUID id;
    private String description;
    private OrderItemStatusEnum status;
    private LocalDateTime arrivedAt;
}
