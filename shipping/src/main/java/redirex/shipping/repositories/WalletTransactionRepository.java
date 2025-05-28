package redirex.shipping.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import redirex.shipping.entity.UserWalletEntity;
import redirex.shipping.entity.WalletTransactionEntity;
import redirex.shipping.enums.WalletTransactionTypeEnum;

import java.util.List;

public interface WalletTransactionRepository extends JpaRepository<WalletTransactionEntity, Long> {
    List<WalletTransactionEntity> findByUserWallet(UserWalletEntity userWallet);
    List<WalletTransactionEntity> findByType(WalletTransactionTypeEnum type);
    List<WalletTransactionEntity> findByRelatedOrderItemId(Long orderItemId);
    List<WalletTransactionEntity> findByRelatedShipmentId(Long shipmentId);

    @Query("SELECT w FROM WalletTransactionEntity w WHERE w.userId.id = :userId")
    List<WalletTransactionEntity> findAllByUserId(@Param("userId") Long userId);
}