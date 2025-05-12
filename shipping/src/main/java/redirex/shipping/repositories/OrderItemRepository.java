package redirex.shipping.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import redirex.shipping.entity.OrderItemEntity;
import redirex.shipping.entity.Shipment;
import redirex.shipping.entity.UserEntity;
import redirex.shipping.enums.OrderItemStatusEnum;

import java.util.List;

public interface OrderItemRepository extends JpaRepository<OrderItemEntity, Long> {
    List<OrderItemEntity> findByUser(UserEntity user);
    List<OrderItemEntity> findByStatus(OrderItemStatusEnum status);
    List<OrderItemEntity> findByShipment(Shipment shipment);
}