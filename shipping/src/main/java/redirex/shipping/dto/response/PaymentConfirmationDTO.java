package redirex.shipping.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record PaymentConfirmationDTO (
    UUID orderItemId,
    UUID shipmentId,
    BigDecimal amountPaid,
    String currency,
    LocalDateTime paymentDate,
    String transactionId
) {

}