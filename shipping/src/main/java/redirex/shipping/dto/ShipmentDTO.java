package redirex.shipping.dto;

import lombok.Data;
import redirex.shipping.enums.OrderItemStatusEnum;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
public class ShipmentDTO {
    private UUID id;
    private UUID userId;
    private List<UUID> orderItemIds;
    private String shippingMethod;
    private BigDecimal shippingCost;
    private BigDecimal insuranceCost;
    private BigDecimal importTaxes;
    private BigDecimal totalShippingPaid;
    private UUID appliedShippingCouponId;
    private String trackingCode;
    private OrderItemStatusEnum status;
    private LocalDateTime paidShippingAt;
    private LocalDateTime shippedAt;
    private LocalDateTime deliveredAt;
    private LocalDateTime createdAt;
}