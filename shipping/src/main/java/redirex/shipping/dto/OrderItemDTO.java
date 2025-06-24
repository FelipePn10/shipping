package redirex.shipping.dto;

import lombok.Data;
import redirex.shipping.enums.CurrencyEnum;
import redirex.shipping.enums.OrderItemStatusEnum;
import java.math.BigDecimal;
import java.time.LocalDateTime;


@Data
public class OrderItemDTO {
    private Long id;
    private String productUrl;
    private BigDecimal productValue;
    private CurrencyEnum originalCurrency;
    private String originCountry;
    private String recipientCpf; // Pode ser mascarado no serviço
    private Long addressId;
    private OrderItemStatusEnum status;
    private LocalDateTime createdAt;
    private LocalDateTime paymentDeadline;
    private LocalDateTime paidProductAt;
    private LocalDateTime arrivedAtWarehouseAt;
    private String warehouseNotes;
    private Double weight;
    private String dimensions;
    private boolean requestedConsolidation;
    private Long shipment;
    private Long user;
    private Long warehouse;
    private String category;
}