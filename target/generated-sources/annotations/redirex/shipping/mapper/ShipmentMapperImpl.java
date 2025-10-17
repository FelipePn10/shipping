package redirex.shipping.mapper;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;
import redirex.shipping.dto.request.ShipmentRequest;
import redirex.shipping.entity.CouponEntity;
import redirex.shipping.entity.ShipmentEntity;
import redirex.shipping.entity.UserCouponEntity;
import redirex.shipping.entity.UserEntity;
import redirex.shipping.entity.UserWalletEntity;
import redirex.shipping.enums.ShipmentEnum;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    comments = "version: 1.6.3, compiler: javac, environment: Java 21.0.8 (Arch Linux)"
)
@Component
public class ShipmentMapperImpl implements ShipmentMapper {

    @Override
    public ShipmentRequest toDTO(ShipmentEntity entity) {
        if ( entity == null ) {
            return null;
        }

        UUID userId = null;
        List<UUID> orderItemIds = null;
        UUID appliedShippingCouponId = null;
        UUID id = null;
        String shippingMethod = null;
        BigDecimal shippingCost = null;
        BigDecimal insuranceCost = null;
        BigDecimal importTaxes = null;
        BigDecimal totalShippingPaid = null;
        String trackingCode = null;
        ShipmentEnum status = null;
        LocalDateTime paidShippingAt = null;
        LocalDateTime shippedAt = null;
        LocalDateTime deliveredAt = null;
        LocalDateTime createdAt = null;

        userId = entityUserWalletWalletId( entity );
        orderItemIds = mapOrderItemIds( entity.getOrderItems() );
        appliedShippingCouponId = entityAppliedShippingCouponCouponId( entity );
        id = entity.getId();
        shippingMethod = entity.getShippingMethod();
        shippingCost = entity.getShippingCost();
        insuranceCost = entity.getInsuranceCost();
        importTaxes = entity.getImportTaxes();
        totalShippingPaid = entity.getTotalShippingPaid();
        trackingCode = entity.getTrackingCode();
        status = entity.getStatus();
        paidShippingAt = entity.getPaidShippingAt();
        shippedAt = entity.getShippedAt();
        deliveredAt = entity.getDeliveredAt();
        createdAt = entity.getCreatedAt();

        ShipmentRequest shipmentRequest = new ShipmentRequest( id, userId, orderItemIds, shippingMethod, shippingCost, insuranceCost, importTaxes, totalShippingPaid, appliedShippingCouponId, trackingCode, status, paidShippingAt, shippedAt, deliveredAt, createdAt );

        return shipmentRequest;
    }

    @Override
    public ShipmentEntity toEntity(ShipmentRequest dto) {
        if ( dto == null ) {
            return null;
        }

        ShipmentEntity.ShipmentEntityBuilder shipmentEntity = ShipmentEntity.builder();

        shipmentEntity.id( dto.id() );
        shipmentEntity.shippingMethod( dto.shippingMethod() );
        shipmentEntity.shippingCost( dto.shippingCost() );
        shipmentEntity.insuranceCost( dto.insuranceCost() );
        shipmentEntity.importTaxes( dto.importTaxes() );
        shipmentEntity.totalShippingPaid( dto.totalShippingPaid() );
        shipmentEntity.trackingCode( dto.trackingCode() );
        shipmentEntity.status( dto.status() );
        shipmentEntity.paidShippingAt( dto.paidShippingAt() );
        shipmentEntity.shippedAt( dto.shippedAt() );
        shipmentEntity.deliveredAt( dto.deliveredAt() );
        shipmentEntity.createdAt( dto.createdAt() );

        return shipmentEntity.build();
    }

    private UUID entityUserWalletWalletId(ShipmentEntity shipmentEntity) {
        UserEntity user = shipmentEntity.getUser();
        if ( user == null ) {
            return null;
        }
        UserWalletEntity wallet = user.getWallet();
        if ( wallet == null ) {
            return null;
        }
        return wallet.getWalletId();
    }

    private UUID entityAppliedShippingCouponCouponId(ShipmentEntity shipmentEntity) {
        UserCouponEntity appliedShippingCoupon = shipmentEntity.getAppliedShippingCoupon();
        if ( appliedShippingCoupon == null ) {
            return null;
        }
        CouponEntity coupon = appliedShippingCoupon.getCoupon();
        if ( coupon == null ) {
            return null;
        }
        return coupon.getId();
    }
}
