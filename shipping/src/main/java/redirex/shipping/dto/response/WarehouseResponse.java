package redirex.shipping.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class WarehouseResponse {
    private Long id;
    private String name;
    private String location;
    private List<Long> orderItemIds;
}