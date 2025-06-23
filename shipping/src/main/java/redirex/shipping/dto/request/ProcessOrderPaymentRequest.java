package redirex.shipping.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ProcessOrderPaymentRequest {
    @NotNull(message = "Order ID is required")
    private Long orderId;
}