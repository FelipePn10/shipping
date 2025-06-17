package redirex.shipping.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import redirex.shipping.entity.WarehouseEntity;

import java.util.Optional;


public interface WarehouseRepository extends JpaRepository<WarehouseEntity, Long> {

    // Método para buscar warehouse por ID de usuário
    @Query("SELECT w FROM WarehouseEntity w WHERE w.userId.id = :userId")
    Optional<WarehouseEntity> findByUserId(@Param("userId") Long userId);

    // Método para carregar warehouse com itens
    @Query("SELECT w FROM WarehouseEntity w LEFT JOIN FETCH w.orderItems WHERE w.id = :id")
    Optional<WarehouseEntity> findByIdWithItems(@Param("id") Long id);
}