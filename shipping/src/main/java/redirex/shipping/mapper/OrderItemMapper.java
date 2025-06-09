package redirex.shipping.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import redirex.shipping.dto.OrderItemDTO;
import redirex.shipping.entity.OrderItemEntity;

@Mapper(componentModel = "spring")
public interface OrderItemMapper {
    @Mapping(source = "category.name", target = "category")
    @Mapping(source = "warehouse.id", target = "warehouseId")
    @Mapping(source = "shipment.id", target = "shipmentId")
    OrderItemDTO toDTO(OrderItemEntity entity);

    @Mapping(source = "category", target = "category.name")
    @Mapping(target = "warehouseId", ignore = true)
    @Mapping(target = "shipmentId", ignore = true)
    @Mapping(target = "userId", ignore = true)
    OrderItemEntity toEntity(OrderItemDTO dto);
}