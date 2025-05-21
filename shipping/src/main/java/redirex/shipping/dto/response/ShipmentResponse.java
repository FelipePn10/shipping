package redirex.shipping.dto.response;

import lombok.Builder;
import lombok.Data;
import redirex.shipping.enums.ShipmentStatuEnum;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class ShipmentResponse {
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
    private ShipmentStatuEnum status;
    private LocalDateTime paidShippingAt;
    private LocalDateTime shippedAt;
    private LocalDateTime deliveredAt;
    private LocalDateTime createdAt;
}