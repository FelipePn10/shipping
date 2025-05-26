package redirex.shipping.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "enterprises", indexes = {
        @Index(name = "idx_enterprise_email", columnList = "email"),
        @Index(name = "idx_enterprise_cnpj", columnList = "cnpj")
})
@NoArgsConstructor
@Getter
@Setter
public class EnterpriseEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Company name is required")
    @Size(max = 255, message = "Company name must not exceed 255 characters")
    @Column(unique = true, nullable = false)
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    @Size(max = 255, message = "Email must not exceed 255 characters")
    @Column(unique = true, nullable = false)
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters")
    @Column(nullable = false)
    private String password;

    @NotBlank (message = "CNPJ is required")
    @Pattern(regexp = "\\d{14}", message = "CPF must be 14 digits")
    @Column(unique = true, nullable = false)
    private String cnpj;

    @NotBlank(message = "Phone is required")
    @Size(max = 20, message = "Phone must not exceed 20 characters")
    @Column(nullable = false)
    private String phone;

    @NotBlank(message = "Address is required")
    @Size(max = 255, message = "Address must not exceed 255 characters")
    @Column(nullable = false)
    private String address;

    @Size(max = 255, message = "Complement must not exceed 255 characters")
    @Column
    private String complement;

    @NotBlank(message = "City is required")
    @Size(max = 100, message = "City must not exceed 100 characters")
    @Column(nullable = false)
    private String city;

    @NotBlank(message = "State is required")
    @Size(max = 100, message = "State must not exceed 100 characters")
    @Column(nullable = false)
    private String state;

    @NotBlank(message = "Zipcode is required")
    @Size(max = 20, message = "Zipcode must not exceed 20 characters")
    @Column(nullable = false)
    private String zipcode;

    @NotBlank(message = "Country is required")
    @Size(max = 100, message = "Country must not exceed 100 characters")
    @Column(nullable = false)
    private String country;

    @NotBlank(message = "Occupation is required")
    @Size(max = 100, message = "Occupation must not exceed 100 characters")
    @Column(nullable = false)
    private String occupation;

    @NotBlank(message = "Role is required")
    @Size(max = 50, message = "Role must not exceed 50 characters")
    @Column(nullable = false)
    private String role;

    @Column
    private String passwordResetToken;

    @Column
    private LocalDateTime passwordResetTokenExpiry;

    public EnterpriseEntity(
            String name,
            String email,
            String password,
            String cnpj,
            String phone,
            String address,
            String complement,
            String city,
            String state,
            String zipcode,
            String country,
            String occupation,
            String role) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.cnpj = cnpj;
        this.phone = phone;
        this.address = address;
        this.complement = complement;
        this.city = city;
        this.state = state;
        this.zipcode = zipcode;
        this.country = country;
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
        if (!(o instanceof EnterpriseEntity that)) return false;
        return Objects.equals(id, that.id) &&
                Objects.equals(email, that.email) &&
                Objects.equals(cnpj, that.cnpj);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, email, cnpj);
    }

    @Override
    public String toString() {
        return "UserEntity{" +
                "walletId=" + id +
                ", fullname='" + name + '\'' +
                ", email='" + email + '\'' +
                ", cpf='" + cnpj + '\'' +
                ", role='" + role + '\'' +
                '}';
    }
}
