package redirex.shipping.service;


import redirex.shipping.entity.OrderItemEntity;

import java.util.List;

public interface OrderItemService {
    void createOrderItem(OrderItemEntity orderItemEntity);

    List<OrderItemEntity> findByOrderId(Long orderId);
}
