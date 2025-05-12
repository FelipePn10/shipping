package redirex.shipping.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import redirex.shipping.entity.ShipmentEntity;
import redirex.shipping.entity.UserEntity;
import redirex.shipping.enums.ShipmentStatuEnum;

import java.util.List;

public interface ShipmentRepository extends JpaRepository<ShipmentEntity, Long> {
    List<ShipmentEntity> findByUser(UserEntity user);
    List<ShipmentEntity> findByStatus(ShipmentStatuEnum status);
    List<ShipmentEntity> findByTrackingCode(String trackingCode);
}