package redirex.shipping.controller.dto.request;

import lombok.Data;
import redirex.shipping.enums.CurrencyEnum;

import java.math.BigDecimal;

@Data
public class DepositRequestDto {
    private BigDecimal amount;
    private CurrencyEnum currency;
    private String sourceCurrency;
}