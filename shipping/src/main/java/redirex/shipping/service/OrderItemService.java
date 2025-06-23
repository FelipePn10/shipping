package redirex.shipping.service;


import jakarta.validation.Valid;
import redirex.shipping.dto.request.CreateOrderItemRequest;
import redirex.shipping.dto.response.OrderItemResponse;
import redirex.shipping.entity.OrderItemEntity;
import redirex.shipping.entity.ProductCategoryEntity;
import redirex.shipping.entity.UserEntity;
import redirex.shipping.entity.WarehouseEntity;

import java.util.List;

public interface OrderItemService {
    OrderItemResponse createOrderItem(Long userId, CreateOrderItemRequest request, UserEntity user, ProductCategoryEntity category, WarehouseEntity warehouse);
    OrderItemResponse processOrderPayment(Long orderItemId, Long userId);
}
