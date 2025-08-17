package redirex.shipping.dto;

import lombok.Data;
import redirex.shipping.enums.CurrencyEnum;
import redirex.shipping.enums.WalletTransactionTypeEnum;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class WalletTransactionDTO {
    private UUID id;
    private UUID userWalletId;
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
}