package redirex.shipping.dto.response;

import java.util.List;
import java.util.UUID;

public record WarehouseResponse (
    UUID id,
    List<OrderItemSummaryResponse> orderItems
) {

}