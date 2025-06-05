package redirex.shipping.dto.response;

import lombok.*;
import redirex.shipping.enums.CurrencyEnum;
import redirex.shipping.enums.WalletTransactionTypeEnum;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class WalletTransactionResponse {
    private Long id;
    private String status;
    private Long userWalletId;
    private Long userId;
    private String fee;
    private String currency;
    private String chargedAmount;
    private BigDecimal amountToChargeInBRL;
    private BigDecimal netAmountInCNY;
    private BigDecimal feeInCNY;
    private LocalDateTime depositRequestTime;
    private BigDecimal chargedCurrency;
    private String transactionDescription;
    private WalletTransactionTypeEnum type;
    private BigDecimal amount;
    private String description;
    private Long relatedOrderItemId;
    private Long relatedShipmentId;
    private BigDecimal exchangeRate;
    private BigDecimal transactionFee;
    private BigDecimal originalAmountDeposited;
    private CurrencyEnum originalCurrencyDeposited;
    private LocalDateTime createdAt;

    public WalletTransactionResponse(String error, String message) {

    }
}