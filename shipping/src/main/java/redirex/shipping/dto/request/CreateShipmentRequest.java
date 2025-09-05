package redirex.shipping.dto.request;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record CreateShipmentRequest(
        @NotBlank(message = "Shipping method is required")
        @Size(max = 50, message = "Shipping method must not exceed 50 characters")
        String shippingMethod,

        @NotNull(message = "Shipping cost is required")
        @DecimalMin(value = "0.0", message = "Shipping cost cannot be negative")
        @Digits(integer = 15, fraction = 4, message = "Shipping cost format is invalid")
        BigDecimal shippingCost,

        @DecimalMin(value = "0.0", message = "Insurance cost cannot be negative")
        @Digits(integer = 15, fraction = 4, message = "Insurance cost format is invalid")
        BigDecimal insuranceCost,

        @DecimalMin(value = "0.0", message = "Import taxes cannot be negative")
        @Digits(integer = 15, fraction = 4, message = "Import taxes format is invalid")
        BigDecimal importTaxes,

        @NotEmpty(message = "Order item IDs are required")
        List<UUID> orderItemIds,

        UUID appliedShippingCouponId
) {
    public CreateShipmentRequest {
        if (orderItemIds != null) {
            orderItemIds = orderItemIds.stream()
                    .filter(id -> id != null)
                    .toList();
        }

        if (insuranceCost == null) {
            insuranceCost = BigDecimal.ZERO;
        }

        if (importTaxes == null) {
            importTaxes = BigDecimal.ZERO;
        }
    }
}