package redirex.shipping.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import redirex.shipping.entity.WarehouseEntity;

public interface WarehouseRepository extends JpaRepository<WarehouseEntity, Long> {
}