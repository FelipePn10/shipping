package redirex.shipping.dto.response;

import lombok.*;
import redirex.shipping.enums.CurrencyEnum;
import redirex.shipping.enums.WalletTransactionTypeEnum;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class WalletTransactionResponse {
    private UUID id;
    private String status;
    private CurrencyEnum chargedCurrency;
    private UUID userWalletId;
    private UUID userId;
    private String fee;
    private String currency;
    private String chargedAmount;
    private BigDecimal amountToChargeInBRL;
    private BigDecimal netAmountInCNY;
    private BigDecimal feeInCNY;
    private LocalDateTime depositRequestTime;
    private String transactionDescription;
    private WalletTransactionTypeEnum type;
    private BigDecimal amount;
    private String description;
    private UUID relatedOrderItemId;
    private UUID relatedShipmentId;
    private BigDecimal exchangeRate;
    private BigDecimal transactionFee;
    private BigDecimal originalAmountDeposited;
    private CurrencyEnum originalCurrencyDeposited;
    private LocalDateTime createdAt;

    public WalletTransactionResponse(String error, String message) {

    }
}