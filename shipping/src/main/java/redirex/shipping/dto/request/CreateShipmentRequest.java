package redirex.shipping.dto.request;

import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
public class CreateShipmentRequest {
    @NotBlank(message = "Shipping method is required")
    @Size(max = 50, message = "Shipping method must not exceed 50 characters")
    private String shippingMethod;

    @NotNull(message = "Shipping cost is required")
    @DecimalMin(value = "0.0", message = "Shipping cost cannot be negative")
    @Digits(integer = 15, fraction = 4, message = "Shipping cost format is invalid")
    private BigDecimal shippingCost;

    @DecimalMin(value = "0.0", message = "Insurance cost cannot be negative")
    @Digits(integer = 15, fraction = 4, message = "Insurance cost format is invalid")
    private BigDecimal insuranceCost;

    @DecimalMin(value = "0.0", message = "Import taxes cannot be negative")
    @Digits(integer = 15, fraction = 4, message = "Import taxes format is invalid")
    private BigDecimal importTaxes;

    @NotEmpty(message = "Order item IDs are required")
    private List<Long> orderItemIds;

    private Long appliedShippingCouponId;
}