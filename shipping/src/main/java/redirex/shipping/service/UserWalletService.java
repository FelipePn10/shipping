package redirex.shipping.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import redirex.shipping.entity.UserEntity;
import redirex.shipping.entity.UserWalletEntity;
import redirex.shipping.enums.CurrencyEnum;
import redirex.shipping.repositories.UserWalletRepository;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class UserWalletService {
    private static final Logger logger = LoggerFactory.getLogger(UserWalletService.class);

    private final UserWalletRepository userWalletRepository;

    @Transactional
    public UserWalletEntity createInitialWallet(UserEntity user, CurrencyEnum currency) {
        logger.info("Creating initial wallet for user: {} with currency: {}", user.getEmail(), currency);

        UserWalletEntity wallet = UserWalletEntity.builder()
                .user(user)
                .currency(currency)
                .balance(BigDecimal.ZERO)
                .build();
        wallet = userWalletRepository.save(wallet);

        logger.info("Initial wallet created for user: {}", user.getEmail());
        return wallet;
    }
}
