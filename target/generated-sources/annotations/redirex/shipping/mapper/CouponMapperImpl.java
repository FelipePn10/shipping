package redirex.shipping.mapper;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;
import redirex.shipping.dto.request.CouponRequest;
import redirex.shipping.entity.CouponEntity;
import redirex.shipping.enums.CouponTypeEnum;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    comments = "version: 1.6.3, compiler: javac, environment: Java 21.0.8 (Arch Linux)"
)
@Component
public class CouponMapperImpl implements CouponMapper {

    @Override
    public CouponRequest toDTO(CouponEntity entity) {
        if ( entity == null ) {
            return null;
        }

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

        code = entity.getCode();
        description = entity.getDescription();
        discountAmount = entity.getDiscountAmount();
        if ( entity.getDiscountPercentage() != null ) {
            discountPercentage = entity.getDiscountPercentage().doubleValue();
        }
        maxDiscountValue = entity.getMaxDiscountValue();
        validFrom = entity.getValidFrom();
        validTo = entity.getValidTo();
        type = entity.getType();
        minPurchaseValue = entity.getMinPurchaseValue();
        isWelcomeCoupon = entity.getIsWelcomeCoupon();
        isNewsletterCoupon = entity.getIsNewsletterCoupon();

        Boolean isActive = null;

        CouponRequest couponRequest = new CouponRequest( code, description, discountAmount, discountPercentage, maxDiscountValue, validFrom, validTo, type, minPurchaseValue, isActive, isWelcomeCoupon, isNewsletterCoupon );

        return couponRequest;
    }

    @Override
    public CouponEntity toEntity(CouponRequest dto) {
        if ( dto == null ) {
            return null;
        }

        CouponEntity.CouponEntityBuilder couponEntity = CouponEntity.builder();

        couponEntity.code( dto.code() );
        couponEntity.description( dto.description() );
        couponEntity.discountAmount( dto.discountAmount() );
        if ( dto.discountPercentage() != null ) {
            couponEntity.discountPercentage( BigDecimal.valueOf( dto.discountPercentage() ) );
        }
        couponEntity.maxDiscountValue( dto.maxDiscountValue() );
        if ( dto.isActive() != null ) {
            couponEntity.isActive( dto.isActive() );
        }
        couponEntity.validFrom( dto.validFrom() );
        couponEntity.validTo( dto.validTo() );
        couponEntity.type( dto.type() );
        couponEntity.minPurchaseValue( dto.minPurchaseValue() );
        couponEntity.isWelcomeCoupon( dto.isWelcomeCoupon() );
        couponEntity.isNewsletterCoupon( dto.isNewsletterCoupon() );

        return couponEntity.build();
    }
}
