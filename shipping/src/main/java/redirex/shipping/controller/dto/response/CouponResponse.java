package redirex.shipping.controller.dto.response;

import lombok.Builder;
import lombok.Data;
import redirex.shipping.enums.CouponTypeEnum;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class CouponResponse {
    private Long id;
    private String code;
    private String description;
    private BigDecimal discountAmount;
    private Double discountPercentage;
    private BigDecimal maxDiscountValue;
    private LocalDateTime validFrom;
    private LocalDateTime validTo;
    private CouponTypeEnum type;
    private BigDecimal minPurchaseValue;
    private Boolean isActive;
    private Boolean isWelcomeCoupon;
    private Boolean isNewsletterCoupon;
}