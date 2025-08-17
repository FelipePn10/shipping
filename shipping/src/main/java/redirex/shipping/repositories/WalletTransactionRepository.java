package redirex.shipping.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import redirex.shipping.entity.UserWalletEntity;
import redirex.shipping.entity.WalletTransactionEntity;
import redirex.shipping.enums.WalletTransactionTypeEnum;

import java.util.List;
import java.util.UUID;

public interface WalletTransactionRepository extends JpaRepository<WalletTransactionEntity, UUID> {
    List<WalletTransactionEntity> findByUserWallet(UserWalletEntity userWallet);
    List<WalletTransactionEntity> findByType(WalletTransactionTypeEnum type);
    List<WalletTransactionEntity> findByRelatedOrderItemId(UUID orderItemId);
    List<WalletTransactionEntity> findByRelatedShipmentId(UUID shipmentId);

    @Query("SELECT w FROM WalletTransactionEntity w WHERE w.userId.id = :userId")
    List<WalletTransactionEntity> findAllByUserId(@Param("userId") UUID userId);
}