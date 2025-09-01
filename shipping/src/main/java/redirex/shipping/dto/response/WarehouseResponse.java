package redirex.shipping.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
@Builder
public class WarehouseResponse {
    private UUID id;
    private List<OrderItemSummaryResponse> orderItems;
}