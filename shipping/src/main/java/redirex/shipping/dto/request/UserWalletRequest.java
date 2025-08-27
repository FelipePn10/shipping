package redirex.shipping.dto.request;

import lombok.Getter;
import lombok.Setter;
import redirex.shipping.enums.CurrencyEnum;


import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class UserWalletRequest {
    private UUID id;
    private UUID userId;
    private UUID walletId;
    private CurrencyEnum currency;
    private BigDecimal balance;
    private LocalDateTime createdAt;
}