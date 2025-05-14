package redirex.shipping.entity;

import jakarta.persistence.*;
import lombok.*;
import redirex.shipping.enums.CurrencyEnum;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "user_coupons")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserCouponEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @Column(name = "coupon_code", nullable = false, unique = true, length = 12)
    private String couponCode;

    @Column(name = "discount_percentage", nullable = false, precision = 5, scale = 2)
    private BigDecimal discountPercentage;

    @Column(name = "currency", nullable = false, length = 3)
    @Enumerated(EnumType.STRING)
    private CurrencyEnum currency;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "expires_at")
    private LocalDateTime expiresAt;

    @Column(name = "is_used", nullable = false)
    private boolean used;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserCouponEntity that)) return false;
        return Objects.equals(id, that.id) &&
                Objects.equals(couponCode, that.couponCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, couponCode);
    }
}