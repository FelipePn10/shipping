package redirex.shipping.entity;

import jakarta.persistence.*;
import lombok.*;
import redirex.shipping.enums.CurrencyEnum;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

@Entity
@Table(name = "user_wallets")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserWalletEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 3)
    private CurrencyEnum currency;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal balance;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserWalletEntity that)) return false;
        return Objects.equals(id, that.id) &&
                Objects.equals(user.getId(), that.user.getId()) &&
                currency == that.currency;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, user.getId(), currency);
    }
}