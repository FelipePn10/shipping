package redirex.shipping.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import redirex.shipping.entity.OrderItemPhotoEntity;

import java.util.UUID;

public interface OrderItemPhotoRepository extends JpaRepository<OrderItemPhotoEntity, UUID> {
    // Métodos de consulta personalizados vão ser adicionados aqui no futuro
    // Exemplo: List<OrderItemPhotoEntity> findByOrderItemId(Long orderItemId);
}
