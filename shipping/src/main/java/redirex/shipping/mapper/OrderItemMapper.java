package redirex.shipping.mapper;


import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import redirex.shipping.dto.OrderItemDTO;
import redirex.shipping.entity.OrderItemEntity;

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface OrderItemMapper {
    @Mapping(source = "userId.id", target = "userId")
    @Mapping(source = "category.name", target = "category")
    @Mapping(source = "warehouseId.id", target = "warehouseId")
    @Mapping(source = "shipmentId.id", target = "shipmentId")
    OrderItemDTO toDTO(OrderItemEntity entity);

    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "warehouseId", ignore = true)
    @Mapping(target = "shipmentId", ignore = true)
    @Mapping(target = "adminAssigned", ignore = true)
    @Mapping(target = "photos", ignore = true)
    @Mapping(source = "category", target = "category.name")
    OrderItemEntity toEntity(OrderItemDTO dto);
}