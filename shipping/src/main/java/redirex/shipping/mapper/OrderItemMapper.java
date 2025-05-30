package redirex.shipping.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import redirex.shipping.dto.OrderItemDTO;
import redirex.shipping.entity.OrderItemEntity;

@Mapper(componentModel = "spring")
public interface OrderItemMapper {
    @Mapping(source = "category.name", target = "categoryName")
    @Mapping(source = "warehouse.id", target = "addressId")
    @Mapping(source = "shipment.id", target = "shipmentId")
    OrderItemDTO toDTO(OrderItemEntity entity);

    @Mapping(source = "categoryName", target = "category.name")
    @Mapping(target = "warehouse", ignore = true)
    @Mapping(target = "shipment", ignore = true)
    @Mapping(target = "user", ignore = true)
    OrderItemEntity toEntity(OrderItemDTO dto);
}