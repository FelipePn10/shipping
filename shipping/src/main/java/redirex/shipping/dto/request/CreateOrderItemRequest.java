package redirex.shipping.dto.request;

import jakarta.validation.constraints.*;
import org.hibernate.validator.constraints.URL;
import redirex.shipping.enums.ProductCategoryEnum;
import redirex.shipping.enums.SizeEnum;

import java.math.BigDecimal;
import java.util.UUID;

public record CreateOrderItemRequest(
        @NotNull(message = "User ID is required")
        UUID userId,

        @NotNull(message = "Warehouse ID is required")
        UUID warehouseId,

        @NotBlank(message = "Recipient CPF is required")
        @Pattern(regexp = "\\d{11}", message = "Recipient CPF must contain exactly 11 digits")
        String recipientCpf,

        @NotBlank(message = "Product URL is required")
        @URL(message = "Product URL must be valid")
        @Size(max = 255, message = "Product URL must not exceed 255 characters")
        String productUrl,

        @NotNull(message = "Category is required")
        ProductCategoryEnum category,

        SizeEnum size,

        @NotBlank
        @Size(max = 255)
        String productName,

        @NotBlank(message = "Description is required")
        @Size(max = 255, message = "Description must not exceed 255 characters")
        String description,

        @NotNull(message = "Quantity is required")
        @Min(value = 1, message = "Quantity must be at least 1")
        Integer quantity,

        BigDecimal productValue,

        boolean autoFetchPrice
) {
    public CreateOrderItemRequest {
        autoFetchPrice = true;
    }
}