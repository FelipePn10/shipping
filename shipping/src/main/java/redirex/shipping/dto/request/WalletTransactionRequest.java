package redirex.shipping.dto.request;

import redirex.shipping.enums.CurrencyEnum;
import redirex.shipping.enums.WalletTransactionTypeEnum;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record WalletTransactionRequest (
    UUID id,
    UUID userWalletId,
    WalletTransactionTypeEnum type,
    BigDecimal amount,
    String description,
    UUID relatedOrderItemId,
    UUID relatedShipmentId,
    BigDecimal exchangeRate,
    BigDecimal transactionFee,
    BigDecimal originalAmountDeposited,
    CurrencyEnum originalCurrencyDeposited,
    LocalDateTime createdAt
) {

}