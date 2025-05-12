package redirex.shipping.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import redirex.shipping.enums.CurrencyEnum;
import redirex.shipping.enums.OrderItemStatusEnum;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class OrderItemEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "User is required")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @NotBlank(message = "Product URL is required")
    @Size(max = 2048, message = "Product URL must not exceed 2048 characters")
    @Column(nullable = false, length = 2048)
    private String productUrl;

    @NotNull(message = "Product value is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Product value must be positive")
    @Digits(integer = 15, fraction = 4, message = "Product value format is invalid")
    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal productValue;

    @NotNull(message = "Original currency is required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 3) // Tamanho suficiente para códigos de moeda (ex: BRL, USD)
    private CurrencyEnum originalCurrency;

    @NotBlank(message = "Origin country is required")
    @Size(max = 100, message = "Origin country must not exceed 100 characters")
    @Column(nullable = false, length = 100)
    private String originCountry;


    @NotNull(message = "Category is required")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_categories", nullable = false)
    private ProductCategoryEntity category;

    @NotBlank(message = "Recipient CPF is required")
    @Pattern(regexp = "\\d{11}", message = "Recipient CPF must contain exactly 11 digits")
    @Column(nullable = false, length = 11)
    private String recipientCpf;

    @NotNull(message = "Shipping address is required")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "addresses", nullable = false)
    private AddressEntity address;

    @NotNull(message = "Status is required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderItemStatusEnum status = OrderItemStatusEnum.CREATING_ORDER; // Valor padrão

    @Column(nullable = false, updatable = false) // Não pode ser nulo, não pode ser atualizado após criação
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Column(nullable = true) // Pode Ser Nulo
    private LocalDateTime paymentDeadline;

    //@ManyToOne(fetch = FetchType.LAZY) // Pode ser nulo, então optional = true (padrão)
    //@JoinColumn(name = "applied_product_coupon_id", nullable = true)
    //private UserCoupon appliedProductCoupon;

    @Column(nullable = true)
    private LocalDateTime paidProductAt;

    @Column(nullable = true)
    private LocalDateTime arrivedAtWarehouseAt;

    @Column(nullable = true, columnDefinition = "TEXT") // Usar TEXT para notas potencialmente longas
    private String warehouseNotes;

    @PositiveOrZero(message = "Weight cannot be negative")
    @Column(nullable = true)
    private Double weight; // Peso em kg, por exemplo

    @Size(max = 50, message = "Dimensions must not exceed 50 characters")
    @Column(nullable = true, length = 50)
    private String dimensions; // Formato "LxWxH" (ex: "10x20x5")

    @Column(nullable = false)
    private boolean requestedConsolidation = false; // Valor padrão

    @ManyToOne(fetch = FetchType.LAZY) // Muitos OrderItems para um Shipment
    @JoinColumn(name = "shipment_id", nullable = true) // Nome da FK na tabela order_items. 'nullable = true' pois um item pode não estar em um envio ainda.
    private Shipment shipment; // Referência ao envio ao qual este item pertence

    // --- Callbacks de Ciclo de Vida JPA ---

    @PrePersist
    protected void onCreate() {
        createdAt = updatedAt = LocalDateTime.now(); // Define data de criação e atualização iniciais
        // Garante que o status inicial seja IN_CART se não for definido explicitamente
        if (status == null) {
            status = OrderItemStatusEnum.IN_CART;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now(); // Atualiza data de modificação
    }

    @Override
    public int hashCode() {
        // Usa a classe e o ID para o hash. Se ID for nulo, usa o hash padrão do objeto.
        return id != null ? Objects.hash(getClass(), id) : super.hashCode();
    }
}

