package redirex.shipping.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class PaymentConfirmationDTO {
    private UUID orderItemId;
    private UUID shipmentId;
    private BigDecimal amountPaid;
    private String currency;
    private LocalDateTime paymentDate;
    private String transactionId;
}