package redirex.shipping.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import redirex.shipping.entity.AdminEntity;
import redirex.shipping.entity.OrderItemEntity;
import redirex.shipping.entity.OrderItemStatusHistoryEntity;
import redirex.shipping.enums.OrderItemStatusEnum;

import java.util.List;

public interface OrderItemRepository extends JpaRepository<OrderItemEntity, Long> {

    // Busca pedidos excluindo status iniciais
    Page<OrderItemEntity> findByStatusNotInOrderByCreatedAtDesc(
            List<OrderItemStatusEnum> excludedStatuses,
            Pageable pageable
    );

    Page<OrderItemEntity> findByAdminAssignedId(Long adminId, Pageable pageable);

    // Busca pedidos por admin
    Page<OrderItemEntity> findByAdminAssignedIdOrderByCreatedAtDesc(
            Long adminId,
            Pageable pageable
    );

    // Metodo para contar pedidos ativos por admin
    long countByAdminAssignedAndStatusIn(AdminEntity admin, List<OrderItemStatusEnum> statuses);
}