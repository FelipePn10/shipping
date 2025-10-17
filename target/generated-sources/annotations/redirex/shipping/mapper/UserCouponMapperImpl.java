package redirex.shipping.mapper;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;
import redirex.shipping.dto.response.CouponResponse;
import redirex.shipping.dto.response.UserCouponResponse;
import redirex.shipping.entity.CouponEntity;
import redirex.shipping.entity.UserCouponEntity;
import redirex.shipping.entity.UserEntity;
import redirex.shipping.entity.UserWalletEntity;
import redirex.shipping.enums.CouponTypeEnum;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    comments = "version: 1.6.3, compiler: javac, environment: Java 21.0.8 (Arch Linux)"
)
@Component
public class UserCouponMapperImpl implements UserCouponMapper {

    @Override
    public UserCouponResponse toDTO(UserCouponEntity entity) {
        if ( entity == null ) {
            return null;
        }

        UUID userId = null;
        UUID id = null;
        CouponResponse coupon = null;
        LocalDateTime usedAt = null;

        userId = entityUserWalletWalletId( entity );
        id = entity.getId();
        coupon = couponEntityToCouponResponse( entity.getCoupon() );
        usedAt = entity.getUsedAt();

        Boolean isUsed = null;
        LocalDateTime assignedAt = null;

        UserCouponResponse userCouponResponse = new UserCouponResponse( id, userId, coupon, isUsed, usedAt, assignedAt );

        return userCouponResponse;
    }

    @Override
    public UserCouponEntity toEntity(UserCouponResponse dto) {
        if ( dto == null ) {
            return null;
        }

        UserCouponEntity.UserCouponEntityBuilder userCouponEntity = UserCouponEntity.builder();

        userCouponEntity.id( dto.id() );
        userCouponEntity.usedAt( dto.usedAt() );
        userCouponEntity.coupon( couponResponseToCouponEntity( dto.coupon() ) );

        return userCouponEntity.build();
    }

    private UUID entityUserWalletWalletId(UserCouponEntity userCouponEntity) {
        UserEntity user = userCouponEntity.getUser();
        if ( user == null ) {
            return null;
        }
        UserWalletEntity wallet = user.getWallet();
        if ( wallet == null ) {
            return null;
        }
        return wallet.getWalletId();
    }

    protected CouponResponse couponEntityToCouponResponse(CouponEntity couponEntity) {
        if ( couponEntity == null ) {
            return null;
        }

        UUID id = null;
        String code = null;
        String description = null;
        BigDecimal discountAmount = null;
        Double discountPercentage = null;
        BigDecimal maxDiscountValue = null;
        LocalDateTime validFrom = null;
        LocalDateTime validTo = null;
        CouponTypeEnum type = null;
        BigDecimal minPurchaseValue = null;
        Boolean isWelcomeCoupon = null;
        Boolean isNewsletterCoupon = null;

        id = couponEntity.getId();
        code = couponEntity.getCode();
        description = couponEntity.getDescription();
        discountAmount = couponEntity.getDiscountAmount();
        if ( couponEntity.getDiscountPercentage() != null ) {
            discountPercentage = couponEntity.getDiscountPercentage().doubleValue();
        }
        maxDiscountValue = couponEntity.getMaxDiscountValue();
        validFrom = couponEntity.getValidFrom();
        validTo = couponEntity.getValidTo();
        type = couponEntity.getType();
        minPurchaseValue = couponEntity.getMinPurchaseValue();
        isWelcomeCoupon = couponEntity.getIsWelcomeCoupon();
        isNewsletterCoupon = couponEntity.getIsNewsletterCoupon();

        Boolean isActive = null;

        CouponResponse couponResponse = new CouponResponse( id, code, description, discountAmount, discountPercentage, maxDiscountValue, validFrom, validTo, type, minPurchaseValue, isActive, isWelcomeCoupon, isNewsletterCoupon );

        return couponResponse;
    }

    protected CouponEntity couponResponseToCouponEntity(CouponResponse couponResponse) {
        if ( couponResponse == null ) {
            return null;
        }

        CouponEntity.CouponEntityBuilder couponEntity = CouponEntity.builder();

        couponEntity.id( couponResponse.id() );
        couponEntity.code( couponResponse.code() );
        couponEntity.description( couponResponse.description() );
        couponEntity.discountAmount( couponResponse.discountAmount() );
        if ( couponResponse.discountPercentage() != null ) {
            couponEntity.discountPercentage( BigDecimal.valueOf( couponResponse.discountPercentage() ) );
        }
        couponEntity.maxDiscountValue( couponResponse.maxDiscountValue() );
        if ( couponResponse.isActive() != null ) {
            couponEntity.isActive( couponResponse.isActive() );
        }
        couponEntity.validFrom( couponResponse.validFrom() );
        couponEntity.validTo( couponResponse.validTo() );
        couponEntity.type( couponResponse.type() );
        couponEntity.minPurchaseValue( couponResponse.minPurchaseValue() );
        couponEntity.isWelcomeCoupon( couponResponse.isWelcomeCoupon() );
        couponEntity.isNewsletterCoupon( couponResponse.isNewsletterCoupon() );

        return couponEntity.build();
    }
}
