package redirex.shipping.dto.request;

import jakarta.persistence.Column;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.URL;
import redirex.shipping.entity.ProductCategoryEntity;
import redirex.shipping.entity.UserEntity;
import redirex.shipping.entity.WarehouseEntity;
import redirex.shipping.enums.CurrencyEnum;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class CreateOrderItemRequest {
    @NotNull(message = "User is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity userId;

    @NotNull(message = "User is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private WarehouseEntity warehouseId;

    @NotBlank
    private BigDecimal recipientCpf;

    @NotNull(message = "User is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private ProductCategoryEntity productCategoryId;

    @NotBlank(message = "Product URL is required")
    @URL(message = "Product URL must be valid")
    @Size(max = 255, message = "Product URL must not exceed 255 characters")
    private String productUrl;

    @NotBlank(message = "Description is required")
    @Size(max = 255, message = "Description must not exceed 255 characters")
    private String description;

    private Float size;

    @NotBlank(message = "Quantity is required")
    private Integer quantity;

    @NotNull(message = "Product value is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Product value must be positive")
    @Digits(integer = 15, fraction = 4, message = "Product value format is invalid")
    private BigDecimal productValue;

    @NotBlank(message = "Category name is required")
    @Size(max = 100, message = "Category name must not exceed 100 characters")
    private String categoryName;

    private LocalDateTime createdAt;

    private LocalDateTime paidProductAt;
}