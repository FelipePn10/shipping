package redirex.shipping.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import redirex.shipping.entity.OrderItemPhotoEntity;

public interface OrderItemPhotoRepository extends JpaRepository<OrderItemPhotoEntity, Long> {
    // Métodos de consulta personalizados vão ser adicionados aqui no futuro
    // Exemplo: List<OrderItemPhotoEntity> findByOrderItemId(Long orderItemId);
}
