package redirex.shipping.dto.request;

import jakarta.validation.constraints.*;
import redirex.shipping.enums.CurrencyEnum;


public record WalletDepositRequest (
    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Amount must be positive")
    @Digits(integer = 15, fraction = 4, message = "Amount format is invalid")
    java.math.BigDecimal amount,

    @NotNull(message = "Wallet currency is required")
    CurrencyEnum walletCurrency,

    @NotNull(message = "Payment currency is required")
    CurrencyEnum paymentCurrency
) {

        }