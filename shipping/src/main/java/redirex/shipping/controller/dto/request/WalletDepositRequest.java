package redirex.shipping.controller.dto.request;

import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Data;
import redirex.shipping.enums.CurrencyEnum;

@Data
@Builder
public class WalletDepositRequest {
    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Amount must be positive")
    @Digits(integer = 15, fraction = 4, message = "Amount format is invalid")
    private java.math.BigDecimal amount;

    @NotNull(message = "Wallet currency is required")
    private CurrencyEnum walletCurrency;

    @NotNull(message = "Payment currency is required")
    private CurrencyEnum paymentCurrency;
}