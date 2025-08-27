package redirex.shipping.dto.request;

import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.URL;
import redirex.shipping.enums.ProductCategoryEnum;
import redirex.shipping.enums.SizeEnum;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@Builder
public class CreateOrderItemRequest {
    @NotNull(message = "User ID is required")
    private UUID userId;

    @NotNull(message = "Warehouse ID is required")
    private UUID warehouseId;

    @NotBlank(message = "Recipient CPF is required")
    @Pattern(regexp = "\\d{11}", message = "Recipient CPF must contain exactly 11 digits")
    private String recipientCpf;

    @NotBlank(message = "Product URL is required")
    @URL(message = "Product URL must be valid")
    @Size(max = 155, message = "Product URL must not exceed 255 characters")
    private String productUrl;

    @NotNull(message = "Category is required")
    private ProductCategoryEnum category;

    private SizeEnum size;

    @NotBlank
    @Size(max = 255)
    private String productName;

    @NotBlank(message = "Description is required")
    @Size(max = 255, message = "Description must not exceed 255 characters")
    private String description;

    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity;

    // Optional field, only used if autoFetchPrice is false
    private BigDecimal productValue;

    @Builder.Default
    private boolean autoFetchPrice = true;
}