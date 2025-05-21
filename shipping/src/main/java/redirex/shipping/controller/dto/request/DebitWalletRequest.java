package redirex.shipping.controller.dto.request;

import jakarta.annotation.Nullable;
import redirex.shipping.entity.OrderItemEntity;
import redirex.shipping.entity.ShipmentEntity;
import redirex.shipping.entity.UserWalletEntity;
import redirex.shipping.enums.CurrencyEnum;
import redirex.shipping.enums.WalletTransactionTypeEnum;

import java.math.BigDecimal;

public class DebitWalletRequest {
    private Long userId;
    private UserWalletEntity userWallet;
    private CurrencyEnum currency;
    private BigDecimal amount;
    private WalletTransactionTypeEnum transactionType;
    private String description;

    @Nullable
    private OrderItemEntity orderItem;

    @Nullable
    private ShipmentEntity shipment;
}

