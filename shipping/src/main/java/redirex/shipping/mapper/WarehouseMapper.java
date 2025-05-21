package redirex.shipping.mapper;

import org.springframework.stereotype.Component;
import redirex.shipping.dto.response.WarehouseResponse;
import redirex.shipping.entity.WarehouseEntity;

@Component
public class WarehouseMapper {

    public WarehouseResponse toResponse(WarehouseEntity warehouse) {
        if (warehouse == null) {
            return null;
        }
        return WarehouseResponse.builder()
                .id(warehouse.getId())
                .build();
    }
}