package redirex.shipping.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import redirex.shipping.enums.CurrencyEnum;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "user_wallets")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserWalletEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long walletId;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;


    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity userId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 3)
    private CurrencyEnum currency;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal balance;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserWalletEntity that)) return false;
        return Objects.equals(walletId, that.walletId) &&
                Objects.equals(userId.getId(), that.userId.getId()) &&
                currency == that.currency;
    }

    @Override
    public int hashCode() {
        return Objects.hash(walletId, userId.getId(), currency);
    }
}