package redirex.shipping.mapper;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;
import redirex.shipping.dto.request.WalletTransactionRequest;
import redirex.shipping.dto.response.WalletTransactionResponse;
import redirex.shipping.entity.UserWalletEntity;
import redirex.shipping.entity.WalletTransactionEntity;
import redirex.shipping.enums.CurrencyEnum;
import redirex.shipping.enums.WalletTransactionTypeEnum;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    comments = "version: 1.6.3, compiler: javac, environment: Java 21.0.8 (Arch Linux)"
)
@Component
public class WalletTransactionMapperImpl implements WalletTransactionMapper {

    @Override
    public WalletTransactionRequest toDTO(WalletTransactionEntity entity) {
        if ( entity == null ) {
            return null;
        }

        UUID userWalletId = null;
        UUID id = null;
        WalletTransactionTypeEnum type = null;
        BigDecimal amount = null;
        UUID relatedOrderItemId = null;
        UUID relatedShipmentId = null;
        BigDecimal exchangeRate = null;
        BigDecimal transactionFee = null;
        BigDecimal originalAmountDeposited = null;
        CurrencyEnum originalCurrencyDeposited = null;
        LocalDateTime createdAt = null;

        userWalletId = entityUserWalletWalletId( entity );
        id = entity.getId();
        type = entity.getType();
        amount = entity.getAmount();
        relatedOrderItemId = entity.getRelatedOrderItemId();
        relatedShipmentId = entity.getRelatedShipmentId();
        exchangeRate = entity.getExchangeRate();
        transactionFee = entity.getTransactionFee();
        originalAmountDeposited = entity.getOriginalAmountDeposited();
        originalCurrencyDeposited = entity.getOriginalCurrencyDeposited();
        createdAt = entity.getCreatedAt();

        String description = null;

        WalletTransactionRequest walletTransactionRequest = new WalletTransactionRequest( id, userWalletId, type, amount, description, relatedOrderItemId, relatedShipmentId, exchangeRate, transactionFee, originalAmountDeposited, originalCurrencyDeposited, createdAt );

        return walletTransactionRequest;
    }

    @Override
    public WalletTransactionEntity toEntity(WalletTransactionRequest dto) {
        if ( dto == null ) {
            return null;
        }

        WalletTransactionEntity.WalletTransactionEntityBuilder walletTransactionEntity = WalletTransactionEntity.builder();

        walletTransactionEntity.id( dto.id() );
        walletTransactionEntity.type( dto.type() );
        walletTransactionEntity.amount( dto.amount() );
        walletTransactionEntity.relatedOrderItemId( dto.relatedOrderItemId() );
        walletTransactionEntity.relatedShipmentId( dto.relatedShipmentId() );
        walletTransactionEntity.exchangeRate( dto.exchangeRate() );
        walletTransactionEntity.transactionFee( dto.transactionFee() );
        walletTransactionEntity.originalAmountDeposited( dto.originalAmountDeposited() );
        walletTransactionEntity.originalCurrencyDeposited( dto.originalCurrencyDeposited() );
        walletTransactionEntity.createdAt( dto.createdAt() );

        return walletTransactionEntity.build();
    }

    @Override
    public WalletTransactionRequest toResponse(WalletTransactionResponse response) {
        if ( response == null ) {
            return null;
        }

        UUID userWalletId = null;
        UUID id = null;
        WalletTransactionTypeEnum type = null;
        String description = null;
        UUID relatedOrderItemId = null;
        UUID relatedShipmentId = null;
        BigDecimal exchangeRate = null;
        BigDecimal transactionFee = null;
        BigDecimal originalAmountDeposited = null;
        CurrencyEnum originalCurrencyDeposited = null;
        LocalDateTime createdAt = null;

        userWalletId = response.userWalletId();
        id = response.id();
        type = response.type();
        description = response.description();
        relatedOrderItemId = response.relatedOrderItemId();
        relatedShipmentId = response.relatedShipmentId();
        exchangeRate = response.exchangeRate();
        transactionFee = response.transactionFee();
        originalAmountDeposited = response.originalAmountDeposited();
        originalCurrencyDeposited = response.originalCurrencyDeposited();
        createdAt = response.createdAt();

        BigDecimal amount = null;

        WalletTransactionRequest walletTransactionRequest = new WalletTransactionRequest( id, userWalletId, type, amount, description, relatedOrderItemId, relatedShipmentId, exchangeRate, transactionFee, originalAmountDeposited, originalCurrencyDeposited, createdAt );

        return walletTransactionRequest;
    }

    private UUID entityUserWalletWalletId(WalletTransactionEntity walletTransactionEntity) {
        UserWalletEntity userWallet = walletTransactionEntity.getUserWallet();
        if ( userWallet == null ) {
            return null;
        }
        return userWallet.getWalletId();
    }
}
