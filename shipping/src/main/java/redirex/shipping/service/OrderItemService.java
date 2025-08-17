package redirex.shipping.service;

import jakarta.validation.Valid;
import redirex.shipping.dto.request.CreateOrderItemRequest;
import redirex.shipping.dto.response.OrderItemResponse;

import java.util.UUID;

public interface OrderItemService {
    OrderItemResponse createOrderItem(UUID userId, @Valid CreateOrderItemRequest request);
    OrderItemResponse processOrderPayment(UUID orderItemId, UUID userId);
}