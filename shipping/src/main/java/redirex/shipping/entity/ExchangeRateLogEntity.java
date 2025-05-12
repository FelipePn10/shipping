package redirex.shipping.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import redirex.shipping.enums.CurrencyEnum;

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
        name = "exchange_rate_logs",
        indexes = {
                @Index(name = "idx_exchange_rate_log_from_currency", columnList = "from_currency"),
                @Index(name = "idx_exchange_rate_log_to_currency", columnList = "to_currency"),
                @Index(name = "idx_exchange_rate_log_fetched_at", columnList = "fetched_at")
        }
)
public class ExchangeRateLogEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "From currency is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "from_currency", nullable = false, length = 3)
    private CurrencyEnum fromCurrency;

    @NotNull(message = "To currency is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "to_currency", nullable = false, length = 3)
    private CurrencyEnum toCurrency;

    @NotNull(message = "Exchange rate is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Exchange rate must be positive")
    @Digits(integer = 15, fraction = 4, message = "Exchange rate format is invalid")
    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal rate;

    @NotBlank(message = "Source is required")
    @Size(max = 100, message = "Source must not exceed 100 characters")
    @Column(nullable = false, length = 100)
    private String source;

    @CreationTimestamp
    @Column(name = "fetched_at", nullable = false, updatable = false)
    private LocalDateTime fetchedAt;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ExchangeRateLogEntity that = (ExchangeRateLogEntity) o;
        return id != null && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return id != null ? Objects.hash(getClass(), id) : super.hashCode();
    }
}