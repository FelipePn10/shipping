package redirex.shipping.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import redirex.shipping.dto.ShipmentDTO;
import redirex.shipping.entity.OrderItemEntity;
import redirex.shipping.entity.ShipmentEntity;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface ShipmentMapper {
    @Mapping(source = "user.wallet.walletId", target = "userId")
    @Mapping(target = "orderItemIds", source = "orderItems", qualifiedByName = "mapOrderItemIds")
    @Mapping(source = "appliedShippingCoupon.coupon.id", target = "appliedShippingCouponId")
    ShipmentDTO toDTO(ShipmentEntity entity);

    @Mapping(target = "user", ignore = true)
    @Mapping(target = "orderItems", ignore = true)
    @Mapping(target = "appliedShippingCoupon", ignore = true)
    ShipmentEntity toEntity(ShipmentDTO dto);

    @Named("mapOrderItemIds")
    default List<UUID> mapOrderItemIds(Set<OrderItemEntity> orderItems) {
        if (orderItems == null) {
            return null;
        }
        return orderItems.stream()
                .map(OrderItemEntity::getId) // Mapeia o ID do OrderItemEntity
                .collect(Collectors.toList()).reversed();
    }
}