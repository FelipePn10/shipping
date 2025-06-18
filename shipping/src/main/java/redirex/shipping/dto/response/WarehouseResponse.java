package redirex.shipping.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class WarehouseResponse {
    private Long id;
    private String name;
    private String location;
    private List<OrderItemSummaryResponse> orderItems;
}