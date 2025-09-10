package redirex.shipping.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import redirex.shipping.enums.CouponTypeEnum;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "coupons")
public class CouponEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToMany(mappedBy = "coupons")
    private Set<UserEntity> users;

    @NotBlank(message = "Coupon code is required")
    @Size(max = 50, message = "Coupon code must not exceed 50 characters")
    @Column(nullable = false, unique = true, length = 50)
    private String code;

    @Size(max = 255, message = "Description must not exceed 255 characters")
    @Column(length = 255)
    private String description;

    @DecimalMin(value = "0.0", message = "Discount amount cannot be negative")
    @Digits(integer = 15, fraction = 4, message = "Discount amount format is invalid")
    @Column(precision = 19, scale = 4)
    private BigDecimal discountAmount;

    @DecimalMin(value = "0.0", message = "Discount percentage cannot be negative")
    @DecimalMax(value = "100.0", message = "Discount percentage cannot exceed 100")
    @Column(precision = 5, scale = 2)
    private BigDecimal discountPercentage;

    @DecimalMin(value = "0.0", message = "Max discount value cannot be negative")
    @Digits(integer = 15, fraction = 4, message = "Max discount value format is invalid")
    @Column(precision = 19, scale = 4)
    private BigDecimal maxDiscountValue;

    @Column(name = "is_active", nullable = false)
    private boolean isActive;

    @NotNull(message = "Valid from date is required")
    @Column(nullable = false)
    private LocalDateTime validFrom;

    @NotNull(message = "Valid to date is required")
    @Column(nullable = false)
    private LocalDateTime validTo;

    @NotNull(message = "Coupon type is required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CouponTypeEnum type = CouponTypeEnum.SHIPPING;

    @DecimalMin(value = "0.0", message = "Minimum purchase value cannot be negative")
    @Digits(integer = 15, fraction = 4, message = "Minimum purchase value format is invalid")
    @Column(precision = 19, scale = 4)
    private BigDecimal minPurchaseValue;

    @NotNull(message = "Is welcome coupon status is required")
    @Column(nullable = false)
    private Boolean isWelcomeCoupon = false;

    @NotNull(message = "Is newsletter coupon status is required")
    @Column(nullable = false)
    private Boolean isNewsletterCoupon = false;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CouponEntity coupon = (CouponEntity) o;
        return id != null && Objects.equals(id, coupon.id);
    }

    @Override
    public int hashCode() {
        return id != null ? Objects.hash(getClass(), id) : super.hashCode();
    }
}