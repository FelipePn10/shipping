package redirex.shipping.dto.response;

import redirex.shipping.enums.CouponTypeEnum;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record CouponResponse (
    UUID id,
    String code,
    String description,
    BigDecimal discountAmount,
    Double discountPercentage,
    BigDecimal maxDiscountValue,
    LocalDateTime validFrom,
    LocalDateTime validTo,
    CouponTypeEnum type,
    BigDecimal minPurchaseValue,
    Boolean isActive,
    Boolean isWelcomeCoupon,
    Boolean isNewsletterCoupon
) {

}