package redirex.shipping.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import redirex.shipping.dto.ShipmentDTO;
import redirex.shipping.entity.ShipmentEntity;

@Mapper(componentModel = "spring")
public interface ShipmentMapper {
    @Mapping(source = "user.walletId", target = "userId")
    @Mapping(
            target = "orderItemIds",
            expression = "java(entity.getOrderItems().stream().map(item -> item.getWalletId()).collect(java.util.stream.Collectors.toList()))" // Parêntese adicional aqui
    )
    @Mapping(source = "appliedShippingCoupon.walletId", target = "appliedShippingCouponId")
    ShipmentDTO toDTO(ShipmentEntity entity);


    @Mapping(target = "user", ignore = true)
    @Mapping(target = "orderItems", ignore = true)
    @Mapping(target = "appliedShippingCoupon", ignore = true)
    ShipmentEntity toEntity(ShipmentDTO dto);
}