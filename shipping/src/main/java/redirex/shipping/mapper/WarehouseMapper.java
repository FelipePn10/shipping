package redirex.shipping.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import redirex.shipping.controller.dto.response.WarehouseResponse;
import redirex.shipping.dto.WarehouseDTO;
import redirex.shipping.entity.WarehouseEntity;

@Mapper(componentModel = "spring")
public interface WarehouseMapper {
    @Mapping(source = "orderItems", target = "orderItemIds", expression = "java(entity.getOrderItems().stream().map(item -> item.getId()).collect(java.util.stream.Collectors.toList()))")
    WarehouseDTO toDTO(WarehouseEntity entity);

    @Mapping(target = "orderItems", ignore = true)
    WarehouseEntity toEntity(WarehouseDTO dto);

    @Mapping(source = "orderItems", target = "orderItemIds", expression = "java(entity.getOrderItems().stream().map(item -> item.getId()).collect(java.util.stream.Collectors.toList()))")
    WarehouseResponse toResponse(WarehouseEntity entity);
}