package redirex.shipping.mapper;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;
import redirex.shipping.dto.request.UserWalletRequest;
import redirex.shipping.entity.UserEntity;
import redirex.shipping.entity.UserWalletEntity;
import redirex.shipping.enums.CurrencyEnum;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    comments = "version: 1.6.3, compiler: javac, environment: Java 21.0.8 (Arch Linux)"
)
@Component
public class UserWalletMapperImpl implements UserWalletMapper {

    @Override
    public UserWalletRequest toDTO(UserWalletEntity entity) {
        if ( entity == null ) {
            return null;
        }

        UUID id = null;
        UUID userId = null;
        UUID walletId = null;
        CurrencyEnum currency = null;
        BigDecimal balance = null;
        LocalDateTime createdAt = null;

        id = entity.getWalletId();
        userId = entityUserIdId( entity );
        walletId = entity.getWalletId();
        currency = entity.getCurrency();
        balance = entity.getBalance();
        createdAt = entity.getCreatedAt();

        UserWalletRequest userWalletRequest = new UserWalletRequest( id, userId, walletId, currency, balance, createdAt );

        return userWalletRequest;
    }

    private UUID entityUserIdId(UserWalletEntity userWalletEntity) {
        UserEntity userId = userWalletEntity.getUserId();
        if ( userId == null ) {
            return null;
        }
        return userId.getId();
    }
}
