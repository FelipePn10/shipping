package redirex.shipping.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.validator.constraints.URL;
import redirex.shipping.enums.OrderItemStatusEnum;
import redirex.shipping.enums.ProductCategoryEnum;
import redirex.shipping.enums.SizeEnum;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Getter
@Setter
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

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotNull(message = "User is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @NotBlank(message = "Product URL is required")
    @URL(message = "Product URL must be valid")
    @Size(max = 255, message = "Product URL must not exceed 255 characters")
    @Column(name = "product_url", nullable = false)
    private String productUrl;

    @NotBlank
    @Size(max = 255)
    @Column(name = "product_name", nullable = false)
    private String productName;

    @NotBlank(message = "Description is required")
    @Size(max = 255, message = "Description must not exceed 255 characters")
    @Column(name = "description", nullable = false)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "size")
    private SizeEnum size;

    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @NotNull(message = "Category is required")
    @Enumerated(EnumType.STRING)
    private ProductCategoryEnum category;

    @NotNull(message = "Product value is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Product value must be positive")
    @Column(name = "product_value", nullable = false, precision = 19, scale = 2)
    private BigDecimal productValue;

    @Pattern(regexp = "\\d{11}", message = "Recipient CPF must be 11 digits")
    @Column(name = "recipient_cpf", nullable = false, length = 11)
    private String recipientCpf;

    @NotNull(message = "Order status is required")
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

    @NotNull(message = "Warehouse is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "warehouse_id", nullable = false)
    private WarehouseEntity warehouse;

    @OneToMany(mappedBy = "orderItem", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItemStatusHistoryEntity> statusHistory = new ArrayList<>();

    @Column(name = "delivered_at")
    private LocalDateTime deliveredAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_assigned_id")
    private AdminEntity adminAssigned;

    @OneToMany(mappedBy = "order_items", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ShipmentEntity> shipments;

    @OneToMany(mappedBy = "orderItem", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItemPhotoEntity> photos = new ArrayList<>();

    public void setWarehouse(WarehouseEntity warehouse) {
        this.warehouse = warehouse;
        if (warehouse != null) {
            warehouse.getOrderItems().add(this);
        }
    }

    public void addPhoto(OrderItemPhotoEntity photo) {
        photos.add(photo);
        photo.setOrderItem(this);
    }

    public void addStatusHistory(OrderItemStatusHistoryEntity history) {
        statusHistory.add(history);
        history.setOrderItem(this);
    }


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