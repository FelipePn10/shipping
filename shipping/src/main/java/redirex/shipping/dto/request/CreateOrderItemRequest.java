package redirex.shipping.dto.request;

import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Data;
import org.hibernate.validator.constraints.URL;
import redirex.shipping.enums.CurrencyEnum;

@Data
@Builder
public class CreateOrderItemRequest {
    @NotBlank(message = "Product URL is required")
    @URL(message = "Product URL must be valid")
    @Size(max = 255, message = "Product URL must not exceed 255 characters")
    private String productUrl;

    @NotNull(message = "Product value is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Product value must be positive")
    @Digits(integer = 15, fraction = 4, message = "Product value format is invalid")
    private java.math.BigDecimal productValue;

    @NotNull(message = "Currency is required")
    private CurrencyEnum originalCurrency;

    @NotBlank(message = "Origin country is required")
    @Size(max = 100, message = "Origin country must not exceed 100 characters")
    private String originCountry;

    @NotBlank(message = "Category name is required")
    @Size(max = 100, message = "Category name must not exceed 100 characters")
    private String categoryName;

    @NotBlank(message = "Recipient CPF is required")
    @Size(max = 14, message = "Recipient CPF must not exceed 14 characters")
    private String recipientCpf;

    @NotNull(message = "Address ID is required")
    private Long addressId;
}