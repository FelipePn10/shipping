package redirex.shipping.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "users", indexes = {
        @Index(name = "idx_user_email", columnList = "email"),
        @Index(name = "idx_user_cpf", columnList = "cpf")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Full name is required")
    @Size(max = 255, message = "Full name must not exceed 255 characters")
    @Column(nullable = false)
    private String fullname;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    @Size(max = 255, message = "Email must not exceed 255 characters")
    @Column(nullable = false)
    private String email;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

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

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "warehouse_id")
    private WarehouseEntity warehouse;

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
            String role) {
        this.fullname = fullname;
        this.email = email;
        this.password = password;
        this.cpf = cpf;
        this.phone = phone;
        this.occupation = occupation;
        this.role = role;
    }

    public void setPassword(String password, PasswordEncoder passwordEncoder) {
        this.password = passwordEncoder.encode(password);
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
        return Objects.hash(id, email, cpf);
    }

    @Override
    public String toString() {
        return "UserEntity{" +
                "id=" + id +
                ", fullname='" + fullname + '\'' +
                ", email='" + email + '\'' +
                ", cpf='" + cpf + '\'' +
                ", role='" + role + '\'' +
                '}';
    }
}