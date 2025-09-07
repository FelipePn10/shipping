package redirex.shipping.dto.response;

import redirex.shipping.enums.CurrencyEnum;
import redirex.shipping.enums.WalletTransactionTypeEnum;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record WalletTransactionResponse(
        UUID id,
        String status,
        CurrencyEnum chargedCurrency,
        UUID userWalletId,
        UUID userId,
        BigDecimal fee,
        CurrencyEnum currency,
        BigDecimal chargedAmount,
        BigDecimal netAmount,
        LocalDateTime depositRequestTime,
        String transactionDescription,
        WalletTransactionTypeEnum type,
        String description,
        UUID relatedOrderItemId,
        UUID relatedShipmentId,
        BigDecimal exchangeRate,
        BigDecimal transactionFee,
        BigDecimal originalAmountDeposited,
        CurrencyEnum originalCurrencyDeposited,
        LocalDateTime createdAt,
        String errorMessage
) {

    public static WalletTransactionResponse createDepositSuccess(
            UUID transactionId,
            UUID userWalletId,
            UUID userId,
            BigDecimal fee,
            CurrencyEnum currency,
            BigDecimal chargedAmount,
            BigDecimal netAmount,
            String transactionDescription,
            LocalDateTime createdAt
    ) {
        return new WalletTransactionResponse(
                transactionId,
                "success",
                CurrencyEnum.BRL,
                userWalletId,
                userId,
                fee,
                currency,
                chargedAmount,
                netAmount,
                null,
                transactionDescription,
                WalletTransactionTypeEnum.DEPOSIT,
                null,
                null,
                null,
                null,
                fee,
                netAmount.add(fee),
                currency,
                createdAt,
                null  // Sem erro
        );
    }

    public static WalletTransactionResponse createError(
            UUID userId,
            String errorMessage
    ) {
        return new WalletTransactionResponse(
                null,
                "error",
                null,
                null,
                userId,
                null,
                null,
                null,
                null,
                null,
                errorMessage,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                LocalDateTime.now(),
                errorMessage
        );
    }
}