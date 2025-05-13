package redirex.shipping.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import redirex.shipping.enums.CurrencyEnum;
import redirex.shipping.enums.OrderItemStatusEnum;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(
        name = "order_items",
        indexes = {
                @Index(name = "idx_order_item_user_id", columnList = "user_id"),
                @Index(name = "idx_order_item_status", columnList = "status")
        }
)
public class OrderItemEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "User is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @NotBlank(message = "Product URL is required")
    @Size(max = 255, message = "Product URL must not exceed 255 characters")
    @Column(name = "product_url", nullable = false)
    private String productUrl;

    @NotNull(message = "Product value is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Product value must be positive")
    @Column(name = "product_value", nullable = false, precision = 19, scale = 4)
    private BigDecimal productValue;

    @NotNull(message = "Original currency is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "original_currency", nullable = false, length = 3)
    private CurrencyEnum originalCurrency;

    @NotBlank(message = "Origin country is required")
    @Size(max = 100, message = "Origin country must not exceed 100 characters")
    @Column(name = "origin_country", nullable = false)
    private String originCountry;

    @NotNull(message = "Category is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private ProductCategoryEntity category;

    @NotBlank(message = "Recipient CPF is required")
    @Size(max = 14, message = "Recipient CPF must not exceed 14 characters")
    @Column(name = "recipient_cpf", nullable = false)
    private String recipientCpf;

    @NotNull(message = "Address is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "address_id", nullable = false)
    private AddressEntity address;

    @NotNull(message = "Status is required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderItemStatusEnum status;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "payment_deadline")
    private LocalDateTime paymentDeadline;

    @Column(name = "paid_product_at")
    private LocalDateTime paidProductAt;

    @Column(name = "arrived_at_warehouse_at")
    private LocalDateTime arrivedAtWarehouseAt;

    @Size(max = 1000, message = "Warehouse notes must not exceed 1000 characters")
    @Column(name = "warehouse_notes", length = 1000)
    private String warehouseNotes;

    @Column
    private Double weight;

    @Size(max = 50, message = "Dimensions must not exceed 50 characters")
    @Column(length = 50)
    private String dimensions;

    @Column(name = "requested_consolidation")
    private boolean requestedConsolidation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shipment_id")
    private ShipmentEntity shipment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "warehouse_id")
    private WarehouseEntity warehouse;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderItemEntity that = (OrderItemEntity) o;
        return id != null && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getClass(), id);
    }
}