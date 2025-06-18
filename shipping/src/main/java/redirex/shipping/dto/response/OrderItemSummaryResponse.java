package redirex.shipping.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import redirex.shipping.enums.OrderItemStatusEnum;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class OrderItemSummaryResponse {
    private Long id;
    private String description;
    private OrderItemStatusEnum status;
    private LocalDateTime arrivedAt;
}
