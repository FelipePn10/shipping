package redirex.shipping.dto;

import lombok.Data;
import redirex.shipping.enums.CurrencyEnum;
import redirex.shipping.enums.WalletTransactionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class WalletTransactionDTO {
    private Long id;
    private Long userWalletId;
    private WalletTransactionType type;
    private BigDecimal amount;
    private String description;
    private Long relatedOrderItemId;
    private Long relatedShipmentId;
    private BigDecimal exchangeRate;
    private BigDecimal transactionFee;
    private BigDecimal originalAmountDeposited;
    private CurrencyEnum originalCurrencyDeposited;
    private LocalDateTime createdAt;
}