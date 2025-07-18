package redirex.shipping.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import redirex.shipping.entity.OrderItemStatusHistoryEntity;

import java.util.List;

public interface OrderItemStatusHistoryRepository
        extends JpaRepository<OrderItemStatusHistoryEntity, Long> {

    List<OrderItemStatusHistoryEntity> findByOrderItem_IdOrderByChangedAtDesc(Long orderItemId);

    Page<OrderItemStatusHistoryEntity> findByOrderItem_IdOrderByChangedAtDesc(
            Long orderItemId,
            Pageable pageable
    );

    // Busca todas as alterações feitas por um admin
    @Query("SELECT h FROM OrderItemStatusHistoryEntity h WHERE h.changedBy.id = :adminId")
    Page<OrderItemStatusHistoryEntity> findHistoryByAdmin(
            @Param("adminId") Long adminId,
            Pageable pageable
    );

    // Busca a última alteração de status para um pedido
    @Query("SELECT h FROM OrderItemStatusHistoryEntity h " +
            "WHERE h.orderItem.id = :orderId " +
            "ORDER BY h.changedAt DESC LIMIT 1")
    OrderItemStatusHistoryEntity findLatestStatusChange(@Param("orderId") Long orderId);
}