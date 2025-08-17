package redirex.shipping.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import redirex.shipping.enums.CurrencyEnum;

import java.util.UUID;

@Data
public class UserWalletRequest {

    @NotNull
    private UUID userId;

    @NotNull
    private CurrencyEnum currency;
}
