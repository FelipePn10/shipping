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
        String fee,
        String currency,
        String chargedAmount,
        BigDecimal amountToChargeInBRL,
        BigDecimal netAmountInCNY,
        BigDecimal feeInCNY,
        LocalDateTime depositRequestTime,
        String transactionDescription,
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

    // Record para respostas de erro
    public record ErrorResponse(String error, String message) {}

    // Método factory para criar resposta de erro
    public static ErrorResponse createError(String error, String message) {
        return new ErrorResponse(error, message);
    }
}