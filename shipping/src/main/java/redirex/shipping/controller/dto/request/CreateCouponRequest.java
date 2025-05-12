package redirex.shipping.controller.dto.request;

import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Data;
import redirex.shipping.enums.CouponTypeEnum;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class CreateCouponRequest {
    @NotBlank(message = "Code is required")
    @Size(max = 50, message = "Code must not exceed 50 characters")
    private String code;

    @Size(max = 255, message = "Description must not exceed 255 characters")
    private String description;

    @DecimalMin(value = "0.0", message = "Discount amount cannot be negative")
    @Digits(integer = 15, fraction = 4, message = "Discount amount format is invalid")
    private BigDecimal discountAmount;

    @DecimalMin(value = "0.0", message = "Discount percentage cannot be negative")
    @DecimalMax(value = "100.0", message = "Discount percentage cannot exceed 100")
    private Double discountPercentage;

    @DecimalMin(value = "0.0", message = "Max discount value cannot be negative")
    @Digits(integer = 15, fraction = 4, message = "Max discount value format is invalid")
    private BigDecimal maxDiscountValue;

    @NotNull(message = "Valid from date is required")
    private LocalDateTime validFrom;

    @NotNull(message = "Valid to date is required")
    private LocalDateTime validTo;

    @NotNull(message = "Coupon type is required")
    private CouponTypeEnum type;

    @DecimalMin(value = "0.0", message = "Minimum purchase value cannot be negative")
    @Digits(integer = 15, fraction = 4, message = "Minimum purchase value format is invalid")
    private BigDecimal minPurchaseValue;

    @NotNull(message = "Is active status is required")
    private Boolean isActive;

    @NotNull(message = "Is welcome coupon status is required")
    private Boolean isWelcomeCoupon;

    @NotNull(message = "Is newsletter coupon status is required")
    private Boolean isNewsletterCoupon;
}