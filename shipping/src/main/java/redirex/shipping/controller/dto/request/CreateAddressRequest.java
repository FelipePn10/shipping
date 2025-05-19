package redirex.shipping.controller.dto.request;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;
import redirex.shipping.entity.AddressEntity;
import redirex.shipping.entity.UserEntity;

@Data
@Builder
public class CreateAddressRequest {
    public enum ResidenceType {
        HOUSE, APARTMENT, COMMERCIAL, OTHER
    }

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

    @NotBlank(message = "Phone is required")
    @Size(max = 100)
    private String phone;

    @Enumerated(EnumType.STRING)
    @NotNull
    private AddressEntity.ResidenceType residenceType = AddressEntity.ResidenceType.HOUSE;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;
}
