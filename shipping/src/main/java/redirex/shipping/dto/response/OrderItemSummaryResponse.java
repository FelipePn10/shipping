package redirex.shipping.dto.response;

import redirex.shipping.enums.OrderItemStatusEnum;

import java.time.LocalDateTime;
import java.util.UUID;

public record OrderItemSummaryResponse (
    UUID id,
    String description,
    OrderItemStatusEnum status,
    LocalDateTime arrivedAt
) {

}
