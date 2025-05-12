package redirex.shipping.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import redirex.shipping.enums.CurrencyEnum;
import redirex.shipping.enums.WalletTransactionType;

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
        name = "wallet_transactions",
        indexes = {
                @Index(name = "idx_wallet_transaction_user_wallet_id", columnList = "user_wallet_id"),
                @Index(name = "idx_wallet_transaction_related_order_item_id", columnList = "related_order_item_id"),
                @Index(name = "idx_wallet_transaction_related_shipment_id", columnList = "related_shipment_id")
        }
)
public class WalletTransactionEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "User wallet is required")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_wallet_id", nullable = false)
    private UserWalletEntity userWalletEntity;

    @NotNull(message = "Transaction type is required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private WalletTransactionType type;

    @NotNull(message = "Amount is required")
    @Digits(integer = 15, fraction = 4, message = "Amount format is invalid")
    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal amount;

    @Column(name = "related_order_item_id")
    private Long relatedOrderItemId;

    @Column(name = "related_shipment_id")
    private Long relatedShipmentId;

    @DecimalMin(value = "0.0", message = "Exchange rate cannot be negative")
    @Digits(integer = 15, fraction = 4, message = "Exchange rate format is invalid")
    @Column(precision = 19, scale = 4)
    private BigDecimal exchangeRate;

    @DecimalMin(value = "0.0", message = "Transaction fee cannot be negative")
    @Digits(integer = 15, fraction = 4, message = "Transaction fee format is invalid")
    @Column(precision = 19, scale = 4)
    private BigDecimal transactionFee;

    @DecimalMin(value = "0.0", message = "Original amount deposited cannot be negative")
    @Digits(integer = 15, fraction = 4, message = "Original amount deposited format is invalid")
    @Column(precision = 19, scale = 4)
    private BigDecimal originalAmountDeposited;

    @Enumerated(EnumType.STRING)
    @Column(length = 3)
    private CurrencyEnum originalCurrencyDeposited;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WalletTransactionEntity that = (WalletTransactionEntity) o;
        return id != null && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return id != null ? Objects.hash(getClass(), id) : super.hashCode();
    }
}