package redirex.shipping.dto;

import lombok.Data;
import redirex.shipping.enums.ShipmentStatusEnum;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class ShipmentDTO {
    private Long id;
    private Long userId;
    private List<Long> orderItemIds;
    private String shippingMethod;
    private BigDecimal shippingCost;
    private BigDecimal insuranceCost;
    private BigDecimal importTaxes;
    private BigDecimal totalShippingPaid;
    private Long appliedShippingCouponId;
    private String trackingCode;
    private ShipmentStatusEnum status;
    private LocalDateTime paidShippingAt;
    private LocalDateTime shippedAt;
    private LocalDateTime deliveredAt;
    private LocalDateTime createdAt;
}