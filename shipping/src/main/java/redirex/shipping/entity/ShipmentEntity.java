package redirex.shipping.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import redirex.shipping.enums.CouponTypeEnum;
import redirex.shipping.enums.OrderItemStatusEnum;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "shipments", indexes = {
        @Index(name = "idx_shipment_user_id", columnList = "user_id"),
        @Index(name = "idx_shipment_status", columnList = "status"),
        @Index(name = "idx_shipment_tracking_code", columnList = "tracking_code")
})
public class ShipmentEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "User is required")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "wallet_id", nullable = false)
    private UserWalletEntity wallet;

    @OneToMany(mappedBy = "shipment")
    private Set<OrderItemEntity> orderItems = new HashSet<>();

    @NotBlank(message = "Shipping method is required")
    @Size(max = 50, message = "Shipping method must not exceed 50 characters")
    @Column(nullable = false, length = 50)
    private String shippingMethod;

    @NotNull(message = "Shipping cost is required")
    @DecimalMin(value = "0.0", message = "Shipping cost cannot be negative")
    @Digits(integer = 15, fraction = 4, message = "Shipping cost format is invalid")
    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal shippingCost = BigDecimal.ZERO;

    @DecimalMin(value = "0.0", message = "Insurance cost cannot be negative")
    @Digits(integer = 15, fraction = 4, message = "Insurance cost format is invalid")
    @Column(precision = 19, scale = 4)
    private BigDecimal insuranceCost;

    @DecimalMin(value = "0.0", message = "Import taxes cannot be negative")
    @Digits(integer = 15, fraction = 4, message = "Import taxes format is invalid")
    @Column(precision = 19, scale = 4)
    private BigDecimal importTaxes;

    @NotNull(message = "Total shipping paid is required")
    @DecimalMin(value = "0.0", message = "Total shipping paid cannot be negative")
    @Digits(integer = 15, fraction = 4, message = "Total shipping paid format is invalid")
    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal totalShippingPaid = BigDecimal.ZERO;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "applied_shipping_coupon_id")
    private UserCouponEntity appliedShippingCoupon;

    @Size(max = 255, message = "Tracking code must not exceed 255 characters")
    @Column(length = 255)
    private String trackingCode;

    @NotNull(message = "Status is required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderItemStatusEnum status = OrderItemStatusEnum.PENDING_SHIPPING_PAYMENT;

    @Column
    private LocalDateTime paidShippingAt;

    @Column
    private LocalDateTime shippedAt;

    @Column
    private LocalDateTime deliveredAt;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    public void addOrderItem(OrderItemEntity orderItem) {
        orderItems.add(orderItem);
        orderItem.setShipment(this);
    }

    public void removeOrderItem(OrderItemEntity orderItem) {
        orderItems.remove(orderItem);
        orderItem.setShipment(null);
    }

    public void applyCoupon(UserCouponEntity userCoupon) {
        if (userCoupon == null) {
            throw new IllegalArgumentException("UserCoupon cannot be null");
        }

        CouponEntity coupon = userCoupon.getCoupon();
        LocalDateTime now = LocalDateTime.now();

        if (userCoupon.isUsed()) {
            throw new IllegalArgumentException("Coupon has already been used");
        }

        if (!coupon.isActive() || now.isBefore(coupon.getValidFrom()) || (coupon.getValidTo() != null && now.isAfter(coupon.getValidTo()))) {
            throw new IllegalArgumentException("Coupon is not active or outside validity period");
        }

        if (coupon.getType() != CouponTypeEnum.SHIPPING) {
            throw new IllegalArgumentException("Coupon is not applicable to shipping");
        }

        if (coupon.getMinPurchaseValue() != null &&
                (totalShippingPaid == null || totalShippingPaid.compareTo(coupon.getMinPurchaseValue()) < 0)) {
            throw new IllegalArgumentException("Shipping cost does not meet minimum purchase value");
        }

        BigDecimal discount = BigDecimal.ZERO;
        if (coupon.getDiscountAmount() != null) {
            discount = coupon.getDiscountAmount();
        } else if (coupon.getDiscountPercentage() != null) {
            BigDecimal discountPercentage = coupon.getDiscountPercentage();
            discount = totalShippingPaid.multiply(
                    discountPercentage.divide(new BigDecimal("100"), 4, BigDecimal.ROUND_HALF_UP)
            );
            if (coupon.getMaxDiscountValue() != null && discount.compareTo(coupon.getMaxDiscountValue()) > 0) {
                discount = coupon.getMaxDiscountValue();
            }
        } else {
            throw new IllegalArgumentException("Coupon has no valid discount defined");
        }

        BigDecimal newTotal = totalShippingPaid.subtract(discount);
        if (newTotal.compareTo(BigDecimal.ZERO) < 0) {
            newTotal = BigDecimal.ZERO;
        }
        this.totalShippingPaid = newTotal;

        this.appliedShippingCoupon = userCoupon;
        userCoupon.setUsed(true);
        userCoupon.setUsedAt(now);
    }

    public void removeCoupon(BigDecimal originalTotalShippingPaid) {
        if (appliedShippingCoupon != null) {
            appliedShippingCoupon.setUsed(false);
            appliedShippingCoupon.setUsedAt(null);
            this.appliedShippingCoupon = null;
        }
        this.totalShippingPaid = originalTotalShippingPaid != null ? originalTotalShippingPaid : BigDecimal.ZERO;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ShipmentEntity shipmentEntity = (ShipmentEntity) o;
        return id != null && Objects.equals(id, shipmentEntity.id);
    }

    @Override
    public int hashCode() {
        return id != null ? Objects.hash(getClass(), id) : super.hashCode();
    }
}