package redirex.shipping.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import redirex.shipping.entity.UserEntity;
import redirex.shipping.entity.UserWalletEntity;
import redirex.shipping.enums.CurrencyEnum;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserWalletRepository extends JpaRepository<UserWalletEntity, UUID> {
    Optional<UserWalletEntity> findByUserIdAndCurrency(UserEntity userId, CurrencyEnum currency);
    List<UserWalletEntity> findByUserId(UserEntity userId);
    boolean existsByUserIdAndCurrency(UserEntity userId, CurrencyEnum currency);
}