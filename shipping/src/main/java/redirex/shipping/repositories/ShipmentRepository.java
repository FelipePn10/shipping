package redirex.shipping.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import redirex.shipping.entity.Shipment;
import redirex.shipping.entity.UserEntity;
import redirex.shipping.enums.ShipmentStatus;

import java.util.List;

public interface ShipmentRepository extends JpaRepository<Shipment, Long> {
    List<Shipment> findByUser(UserEntity user);
    List<Shipment> findByStatus(ShipmentStatus status);
    List<Shipment> findByTrackingCode(String trackingCode);
}