package redirex.shipping.dto.request;

import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.URL;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class CreateOrderItemRequest {
    @NotNull(message = "User ID is required")
    private Long userId;

    @NotNull(message = "Warehouse ID is required")
    private Long warehouseId;

    @NotBlank(message = "Recipient CPF is required")
    @Size(max = 14, message = "Recipient CPF must not exceed 14 characters")
    private String recipientCpf;

    @NotNull(message = "Product category ID is required")
    private Long productCategoryId;

    @NotBlank(message = "Product URL is required")
    @URL(message = "Product URL must be valid")
    @Size(max = 255, message = "Product URL must not exceed 255 characters")
    private String productUrl;

    @NotBlank(message = "Description is required")
    @Size(max = 255, message = "Description must not exceed 255 characters")
    private String description;

    private Float size;

    @NotNull(message = "Quantity is required")
    private Integer quantity;

    @Builder.Default
    private boolean autoFetchPrice = true;

    private LocalDateTime createdAt;

    private LocalDateTime paidProductAt;
}