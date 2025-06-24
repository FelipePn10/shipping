package redirex.shipping.service;

import jakarta.validation.Valid;
import redirex.shipping.dto.request.CreateOrderItemRequest;
import redirex.shipping.dto.response.OrderItemResponse;

public interface OrderItemService {
    OrderItemResponse createOrderItem(Long userId, @Valid CreateOrderItemRequest request);
    OrderItemResponse processOrderPayment(Long orderItemId, Long userId);
}