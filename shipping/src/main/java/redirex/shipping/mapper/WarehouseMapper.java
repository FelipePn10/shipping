package redirex.shipping.mapper;

import org.springframework.stereotype.Component;
import redirex.shipping.controller.dto.response.WarehouseResponse;
import redirex.shipping.entity.WarehouseEntity;

@Component
public class WarehouseMapper {

    public WarehouseResponse toResponse(WarehouseEntity warehouse) {
        if (warehouse == null) {
            return null;
        }
        return WarehouseResponse.builder()
                .id(warehouse.getId())
                .name(warehouse.getName())
                .location(warehouse.getLocation())
                .build();
    }
}