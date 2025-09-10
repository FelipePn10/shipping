package redirex.shipping.repositories;

import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import redirex.shipping.entity.AdminEntity;
import redirex.shipping.entity.OrderItemEntity;
import redirex.shipping.entity.OrderItemStatusHistoryEntity;
import redirex.shipping.entity.UserEntity;
import redirex.shipping.enums.OrderItemStatusEnum;

import java.util.List;
import java.util.UUID;

public interface OrderItemRepository extends JpaRepository<OrderItemEntity, UUID> {

    // Busca pedidos excluindo status iniciais
    Page<OrderItemEntity> findByStatusNotInOrderByCreatedAtDesc(
            List<OrderItemStatusEnum> excludedStatuses,
            Pageable pageable
    );

    Page<OrderItemEntity> findByAdminAssignedId(UUID adminId, Pageable pageable);

    // Metodo para contar pedidos ativos por admin
    long countByAdminAssignedAndStatusIn(AdminEntity admin, List<OrderItemStatusEnum> statuses);

    @Query("SELECT o FROM OrderItemEntity o WHERE o.user = :user AND o.status <> :status")
    List<OrderItemEntity> findByUserAndStatusNot(@Param("user") UserEntity user, @Param("status") OrderItemStatusEnum status);
}