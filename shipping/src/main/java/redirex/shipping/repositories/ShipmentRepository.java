package redirex.shipping.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import redirex.shipping.entity.ShipmentEntity;
import redirex.shipping.entity.UserEntity;
import redirex.shipping.enums.OrderItemStatusEnum;

import java.util.List;
import java.util.UUID;

public interface ShipmentRepository extends JpaRepository<ShipmentEntity, UUID> {
    List<ShipmentEntity> findByUser(UserEntity user);
    List<ShipmentEntity> findByStatus(OrderItemStatusEnum status);
    List<ShipmentEntity> findByTrackingCode(String trackingCode);
}