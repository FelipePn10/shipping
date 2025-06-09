package redirex.shipping.dto.request;

import lombok.Data;
import lombok.NoArgsConstructor;
import redirex.shipping.enums.CurrencyEnum;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
public class DepositRequestDto {

    @NotNull(message = "O valor do depósito não pode ser nulo.")
    @DecimalMin(value = "0.01", message = "O valor do depósito deve ser maior que zero.")
    private BigDecimal amount;
    @NotNull(message = "A moeda alvo do depósito não pode ser nula.")
    private CurrencyEnum currency; // Representa a moeda alvo (CNY no seu serviço)

    private String sourceCurrency; // Representa a moeda original da transação, se diferente da moeda alvo

    @NotBlank(message = "O ID do PaymentMethod do Stripe é obrigatório para o depósito.")
    private String paymentMethodId;
}