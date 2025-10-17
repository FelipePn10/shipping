package redirex.shipping.dto.request;

import redirex.shipping.enums.ShipmentEnum;

import java.util.UUID;

public record AddShipmentToOrderRequest(
        UUID orderId,
        ShipmentEnum status
) {
}
