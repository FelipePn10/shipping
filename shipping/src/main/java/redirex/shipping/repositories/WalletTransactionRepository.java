package redirex.shipping.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import redirex.shipping.entity.UserWallet;
import redirex.shipping.entity.WalletTransaction;
import redirex.shipping.enums.WalletTransactionType;

import java.util.List;

public interface WalletTransactionRepository extends JpaRepository<WalletTransaction, Long> {
    List<WalletTransaction> findByUserWallet(UserWallet userWallet);
    List<WalletTransaction> findByType(WalletTransactionType type);
    List<WalletTransaction> findByRelatedOrderItemId(Long orderItemId);
    List<WalletTransaction> findByRelatedShipmentId(Long shipmentId);
}