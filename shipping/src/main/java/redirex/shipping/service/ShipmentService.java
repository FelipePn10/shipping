package redirex.shipping.service;

import jakarta.validation.Valid;
import redirex.shipping.dto.request.AddShipmentToOrderRequest;
import redirex.shipping.dto.request.PaymentShipmentRequest;
import redirex.shipping.dto.response.AddShipmentToOrderResponse;
import redirex.shipping.dto.response.PaymentShipmentResponse;

import java.util.UUID;

public interface ShipmentService {
    // Alterar Response e Request para os métodos, apenas esboço por enquanto
    AddShipmentToOrderResponse addShipmentToOrder(UUID orderId, @Valid AddShipmentToOrderRequest request);
    PaymentShipmentResponse    processOrderPayment(UUID orderId, @Valid PaymentShipmentRequest request);
    void applyCouponToShipment(UUID shipmentId, UUID userCouponId);
}
