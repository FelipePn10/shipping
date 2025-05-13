package redirex.shipping.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import redirex.shipping.entity.UserEntity;
import redirex.shipping.entity.UserWalletEntity;
import redirex.shipping.enums.CurrencyEnum;

import java.util.List;
import java.util.Optional;

public interface UserWalletRepository extends JpaRepository<UserWalletEntity, Long> {
    @Query("SELECT uw FROM UserWalletEntity uw JOIN uw.balances b WHERE uw.user = :user AND b.currency = :currency")
    Optional<UserWalletEntity> findByUserAndCurrency(
            @Param("user") UserEntity user,
            @Param("currency") CurrencyEnum currency
    );

    List<UserWalletEntity> findByUser(UserEntity user);
}