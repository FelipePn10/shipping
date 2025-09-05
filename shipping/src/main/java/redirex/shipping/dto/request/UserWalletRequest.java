package redirex.shipping.dto.request;

import redirex.shipping.enums.CurrencyEnum;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record UserWalletRequest (
    UUID id,
    UUID userId,
    UUID walletId,
    CurrencyEnum currency,
    BigDecimal balance,
    LocalDateTime createdAt
) {

}