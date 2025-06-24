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
    @Mapping(source = "user.id", target = "user")
    @Mapping(source = "category.name", target = "category")
    @Mapping(source = "warehouse.id", target = "warehouse")
    @Mapping(source = "shipment.id", target = "shipment")
    OrderItemDTO toDTO(OrderItemEntity entity);

    @Mapping(target = "user", ignore = true)
    @Mapping(target = "warehouse", ignore = true)
    @Mapping(target = "shipment", ignore = true)
    @Mapping(target = "adminAssigned", ignore = true)
    @Mapping(target = "photos", ignore = true)
    @Mapping(source = "category", target = "category.name")
    OrderItemEntity toEntity(OrderItemDTO dto);
}