package redirex.shipping.mapper;


import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import redirex.shipping.dto.request.CreateOrderItemRequest;
import redirex.shipping.dto.response.OrderItemResponse;
import redirex.shipping.entity.OrderItemEntity;

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface OrderItemMapper {
    @Mapping(source = "user.id", target = "userId")
    @Mapping(target = "category", ignore = true)
    @Mapping(source = "warehouse.id", target = "warehouseId")
    CreateOrderItemRequest toDTO(OrderItemEntity entity);

    @Mapping(target = "user", ignore = true)
    @Mapping(target = "warehouse", ignore = true)
    @Mapping(source = "category", target = "category")
    OrderItemEntity toEntity(OrderItemResponse dto);
}