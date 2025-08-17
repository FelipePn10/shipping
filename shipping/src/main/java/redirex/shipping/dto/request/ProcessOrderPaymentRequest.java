package redirex.shipping.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class ProcessOrderPaymentRequest {
    @NotNull(message = "Order ID is required")
    private UUID orderId;
}