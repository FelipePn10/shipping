package redirex.shipping.dto.request;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record ProcessOrderPaymentRequest (
        @NotNull(message = "Order ID is required")
        UUID orderId
) {

}