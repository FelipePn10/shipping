package redirex.shipping.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
public class WarehouseResponse {
    private UUID id;
    private String name;
    private String location;
    private List<OrderItemSummaryResponse> orderItems;
}