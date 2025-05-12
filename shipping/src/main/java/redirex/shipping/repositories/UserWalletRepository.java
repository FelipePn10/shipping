package redirex.shipping.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import redirex.shipping.entity.UserEntity;
import redirex.shipping.entity.UserWallet;
import redirex.shipping.enums.CurrencyEnum;

import java.util.List;
import java.util.Optional;

public interface UserWalletRepository extends JpaRepository<UserWallet, Long> {
    Optional<UserWallet> findByUserAndCurrency(UserEntity user, CurrencyEnum currency);
    List<UserWallet> findByUser(UserEntity user);
}