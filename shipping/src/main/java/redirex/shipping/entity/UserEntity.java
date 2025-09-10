package redirex.shipping.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.*;

@Entity
@Table(name = "users", indexes = {
        @Index(name = "idx_user_email", columnList = "email"),
        @Index(name = "idx_user_cpf", columnList = "cpf")
})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserEntity implements Serializable {
    @Id
    @GeneratedValue(generator = "UUID")
    @Column(name = "user_id")
    private UUID id;

    @NotBlank(message = "Full name is required")
    @Size(max = 255, message = "Full name must not exceed 255 characters")
    @Column(nullable = false)
    private String fullname;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    @Size(max = 255, message = "Email must not exceed 255 characters")
    @Column(nullable = false)
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters")
    @Column(nullable = false)
    private String password;

    @NotBlank(message = "CPF is required")
    @Pattern(regexp = "\\d{11}", message = "CPF must be 11 digits")
    @Column(unique = true, nullable = false)
    private String cpf;

    @NotBlank(message = "Phone is required")
    @Size(max = 20, message = "Phone must not exceed 20 characters")
    @Column(nullable = false)
    private String phone;

    @NotBlank(message = "Occupation is required")
    @Size(max = 100, message = "Occupation must not exceed 100 characters")
    @Column(nullable = false)
    private String occupation;

    @NotBlank(message = "Role is required")
    @Size(max = 50, message = "Role must not exceed 50 characters")
    @Column(nullable = false)
    private String role;

    @OneToOne(
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @JoinColumn(name = "warehouse_id")
    private WarehouseEntity warehouse;

    @ManyToMany
    @JoinTable(
            name = "user_coupons",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "coupon_id")
    )
    private Set<CouponEntity> coupons = new HashSet<>();
    @OneToOne(
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @JoinColumn(name = "wallet_id")
    private UserWalletEntity wallet;


    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column
    private String passwordResetToken;

    @Column
    private LocalDateTime passwordResetTokenExpiry;


    @OneToMany(
            mappedBy = "user",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<AddressEntity> addresses = new ArrayList<>();

    public UserEntity(
            String fullname,
            String email,
            String password,
            String cpf,
            String phone,
            String occupation,
            LocalDateTime createdAt,
            LocalDateTime updatedAt,
            String role) {
        this.fullname = fullname;
        this.email = email;
        this.password = password;
        this.cpf = cpf;
        this.phone = phone;
        this.occupation = occupation;
        this.role = role;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    @PrePersist
    @PreUpdate
    private void validate() {
        if (password == null || password.isEmpty()) {
            throw new IllegalStateException("Password cannot be null or empty");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserEntity that)) return false;
        return Objects.equals(id, that.id) &&
                Objects.equals(email, that.email) &&
                Objects.equals(cpf, that.cpf);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, email, cpf, password);
    }

    @Override
    public String toString() {
        return "UserEntity{" +
                "walletId=" + id +
                ", fullname='" + fullname + '\'' +
                ", email='" + email + '\'' +
                ", cpf='" + cpf + '\'' +
                ", role='" + role + '\'' +
                '}';
    }

    @OneToMany(mappedBy = "user")
    private Collection<OrderItemEntity> orderItemEntity;
}