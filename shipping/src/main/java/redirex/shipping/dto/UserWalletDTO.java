package redirex.shipping.dto;

import lombok.Data;
import redirex.shipping.enums.CurrencyEnum;


import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class UserWalletDTO {
    private UUID id;
    private UUID userId;
    private UUID walletId;
    private CurrencyEnum currency;
    private BigDecimal balance;
    private LocalDateTime createdAt;
}