package redirex.shipping.dto.request;

import jakarta.validation.constraints.*;
import redirex.shipping.enums.CouponTypeEnum;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CreateCouponRequest (
    @NotBlank(message = "Code is required")
    @Size(max = 50, message = "Code must not exceed 50 characters")
    String code,

    @Size(max = 255, message = "Description must not exceed 255 characters")
    String description,

    @DecimalMin(value = "0.0", message = "Discount amount cannot be negative")
    @Digits(integer = 15, fraction = 4, message = "Discount amount format is invalid")
    BigDecimal discountAmount,

    @DecimalMin(value = "0.0", message = "Discount percentage cannot be negative")
    @DecimalMax(value = "100.0", message = "Discount percentage cannot exceed 100")
    Double discountPercentage,

    @DecimalMin(value = "0.0", message = "Max discount value cannot be negative")
    @Digits(integer = 15, fraction = 4, message = "Max discount value format is invalid")
    BigDecimal maxDiscountValue,

    @NotNull(message = "Valid from date is required")
    LocalDateTime validFrom,

    @NotNull(message = "Valid to date is required")
    LocalDateTime validTo,

    @NotNull(message = "Coupon type is required")
    CouponTypeEnum type,

    @DecimalMin(value = "0.0", message = "Minimum purchase value cannot be negative")
    @Digits(integer = 15, fraction = 4, message = "Minimum purchase value format is invalid")
    BigDecimal minPurchaseValue,

    @NotNull(message = "Is active status is required")
    Boolean isActive,

    @NotNull(message = "Is welcome coupon status is required")
    Boolean isWelcomeCoupon,

    @NotNull(message = "Is newsletter coupon status is required")
    Boolean isNewsletterCoupon
) {

        }