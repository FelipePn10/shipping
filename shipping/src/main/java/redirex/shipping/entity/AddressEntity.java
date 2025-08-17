package redirex.shipping.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@Table(name = "addresses")
public class AddressEntity {
    public enum ResidenceType {
        HOUSE, APARTMENT, COMMERCIAL, OTHER
    }

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotBlank(message = "Recipient name is required")
    @Size(max = 255)
    private String recipientName;

    @NotBlank(message = "Street is required")
    @Size(max = 255)
    private String street;

    @Size(max = 255)
    private String complement;

    @NotBlank(message = "City is required")
    @Size(max = 100)
    private String city;

    @NotBlank(message = "State is required")
    @Size(max = 100)
    private String state;

    @NotBlank(message = "Zipcode is required")
    @Size(max = 20)
    private String zipcode;

    @NotBlank(message = "Country is required")
    @Size(max = 100)
    private String country;

    @NotNull
    private LocalDateTime createdAt;

    @NotBlank(message = "Phone is required")
    @Size(max = 100)
    private String phone;

    @Enumerated(EnumType.STRING)
    @NotNull
    private ResidenceType residenceType = ResidenceType.HOUSE;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

}
