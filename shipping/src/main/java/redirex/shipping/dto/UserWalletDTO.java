package redirex.shipping.dto;

import lombok.Data;
import redirex.shipping.enums.CurrencyEnum;


import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class UserWalletDTO {
    private Long id;
    private Long userId;
    private CurrencyEnum currency;
    private BigDecimal balance;
    private LocalDateTime createdAt;
}