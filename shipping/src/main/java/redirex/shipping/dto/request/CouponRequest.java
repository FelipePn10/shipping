package redirex.shipping.dto.request;

import redirex.shipping.enums.CouponTypeEnum;


import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CouponRequest (
//  UUID id,
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