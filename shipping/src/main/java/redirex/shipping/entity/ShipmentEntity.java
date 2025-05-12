package redirex.shipping.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import redirex.shipping.enums.CouponTypeEnum;
import redirex.shipping.enums.ShipmentStatus;

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

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "User is required")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @OneToMany(
            mappedBy = "shipment",
            cascade = {CascadeType.PERSIST, CascadeType.MERGE},
            fetch = FetchType.LAZY
    )
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
    private ShipmentStatus status = ShipmentStatus.PENDING_PAYMENT;

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

    // Método auxiliar para adicionar OrderItemEntity
    public void addOrderItem(OrderItemEntity orderItem) {
        orderItems.add(orderItem);
        orderItem.setShipmentEntity(this);
    }

    // Método auxiliar para remover OrderItemEntity
    public void removeOrderItem(OrderItemEntity orderItem) {
        orderItems.remove(orderItem);
        orderItem.setShipmentEntity(null);
    }

    /**
     * Aplica um cupom ao frete, validando as condições e atualizando totalShippingPaid.
     * @param userCoupon O cupom a ser aplicado
     * @throws IllegalArgumentException se o cupom for inválido ou não aplicável
     */
    public void applyCoupon(UserCouponEntity userCoupon) {
        if (userCoupon == null) {
            throw new IllegalArgumentException("UserCoupon cannot be null");
        }

        CouponEntity coupon = userCoupon.getCoupon();
        LocalDateTime now = LocalDateTime.now();

        // Validação: Cupom não usado
        if (userCoupon.getIsUsed()) {
            throw new IllegalArgumentException("Coupon has already been used");
        }

        // Validação: Cupom ativo e dentro do período de validade
        if (!coupon.getIsActive() || now.isBefore(coupon.getValidFrom()) || now.isAfter(coupon.getValidTo())) {
            throw new IllegalArgumentException("Coupon is not active or outside validity period");
        }

        // Validação: Tipo de cupom (apenas SHIPPING)
        if (coupon.getType() != CouponTypeEnum.SHIPPING) {
            throw new IllegalArgumentException("Coupon is not applicable to shipping");
        }

        // Validação: Valor mínimo de compra (se definido)
        if (coupon.getMinPurchaseValue() != null &&
                (totalShippingPaid == null || totalShippingPaid.compareTo(coupon.getMinPurchaseValue()) < 0)) {
            throw new IllegalArgumentException("Shipping cost does not meet minimum purchase value");
        }

        // Cálculo do desconto
        BigDecimal discount = BigDecimal.ZERO;
        if (coupon.getDiscountAmount() != null) {
            // Desconto fixo
            discount = coupon.getDiscountAmount();
        } else if (coupon.getDiscountPercentage() != null) {
            // Desconto percentual
            discount = totalShippingPaid.multiply(BigDecimal.valueOf(coupon.getDiscountPercentage() / 100.0));
            if (coupon.getMaxDiscountValue() != null && discount.compareTo(coupon.getMaxDiscountValue()) > 0) {
                discount = coupon.getMaxDiscountValue();
            }
        } else {
            throw new IllegalArgumentException("Coupon has no valid discount defined");
        }

        // Aplica o desconto, garantindo que totalShippingPaid não seja negativo
        BigDecimal newTotal = totalShippingPaid.subtract(discount);
        if (newTotal.compareTo(BigDecimal.ZERO) < 0) {
            newTotal = BigDecimal.ZERO;
        }
        this.totalShippingPaid = newTotal;

        // Associa o cupom e marca como usado
        this.appliedShippingCoupon = userCoupon;
        userCoupon.setIsUsed(true);
        userCoupon.setUsedAt(now);
    }

    /**
     * Remove o cupom aplicado, restaurando o totalShippingPaid original.
     * @param originalTotalShippingPaid O valor original antes do desconto
     */
    public void removeCoupon(BigDecimal originalTotalShippingPaid) {
        if (appliedShippingCoupon != null) {
            appliedShippingCoupon.setIsUsed(false);
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