package redirex.shipping.dto.request;

import jakarta.annotation.Nullable;
import redirex.shipping.entity.OrderItemEntity;
import redirex.shipping.entity.ShipmentEntity;
import redirex.shipping.entity.UserWalletEntity;
import redirex.shipping.enums.CurrencyEnum;
import redirex.shipping.enums.WalletTransactionTypeEnum;

import java.math.BigDecimal;
import java.util.UUID;

public record DebitWalletRequest (
    UUID userId,
    UserWalletEntity userWallet,
    CurrencyEnum currency,
    BigDecimal amount,
    WalletTransactionTypeEnum transactionType,
    String description,

    @Nullable
    OrderItemEntity orderItem,

    @Nullable
    ShipmentEntity shipment
) {

}

