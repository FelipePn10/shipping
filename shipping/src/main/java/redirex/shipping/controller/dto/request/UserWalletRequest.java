package redirex.shipping.controller.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import redirex.shipping.enums.CurrencyEnum;

@Data
public class UserWalletRequest {

    @NotNull
    private Long userId;

    @NotNull
    private CurrencyEnum currency;
}
