package redirex.shipping.service;


import jakarta.validation.Valid;
import redirex.shipping.dto.request.CreateOrderItemRequest;
import redirex.shipping.dto.response.OrderItemResponse;
import redirex.shipping.entity.OrderItemEntity;

import java.util.List;

public interface OrderItemService {
    OrderItemResponse createOrderItem(@Valid CreateOrderItemRequest dto);
    OrderItemResponse processOrderPayment(Long orderItemId);
}
