package redirex.shipping.dto.request;

import redirex.shipping.enums.ShipmentEnum;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record ShipmentRequest(
        UUID id,
        UUID userId,
        List<UUID> orderItemIds,
        String shippingMethod,
        BigDecimal shippingCost,
        BigDecimal insuranceCost,
        BigDecimal importTaxes,
        BigDecimal totalShippingPaid,
        UUID appliedShippingCouponId,
        String trackingCode,
        ShipmentEnum status,
        LocalDateTime paidShippingAt,
        LocalDateTime shippedAt,
        LocalDateTime deliveredAt,
        LocalDateTime createdAt
) {
}