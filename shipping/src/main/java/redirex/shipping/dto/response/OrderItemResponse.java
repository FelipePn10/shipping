package redirex.shipping.dto.response;

import lombok.Builder;
import lombok.Data;
import redirex.shipping.enums.CurrencyEnum;
import redirex.shipping.enums.OrderItemStatusEnum;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class OrderItemResponse {
    private String productUrl;
    private BigDecimal productValue;
    private CurrencyEnum originalCurrency;
    private String originCountry;
    private String categoryName;
    private String recipientCpf; // Pode ser mascarado no serviço
    private Long addressId;
    private OrderItemStatusEnum status;
    private LocalDateTime createdAt;
    private LocalDateTime paymentDeadline;
    private LocalDateTime paidProductAt;
    private LocalDateTime arrivedAtWarehouseAt;
    private boolean requestedConsolidation;
    private Long shipmentId;
}