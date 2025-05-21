package redirex.shipping.dto.response;

import lombok.Builder;
import lombok.Data;
import redirex.shipping.enums.CurrencyEnum;
import redirex.shipping.enums.WalletTransactionTypeEnum;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class WalletTransactionResponse {
    private Long id;
    private Long userWalletId;
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
}