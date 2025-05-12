package redirex.shipping.controller.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class PaymentConfirmationDTO {
    private Long orderItemId;
    private Long shipmentId;
    private BigDecimal amountPaid;
    private String currency;
    private LocalDateTime paymentDate;
    private String transactionId;
}