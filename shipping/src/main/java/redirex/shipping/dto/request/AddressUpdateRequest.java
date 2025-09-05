package redirex.shipping.dto.request;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import redirex.shipping.entity.AddressEntity;

import java.util.UUID;

public record AddressUpdateRequest(
        @NotBlank(message = "Recipient name is required")
        @Size(max = 255)
        String recipientName,

        @NotBlank(message = "Street is required")
        @Size(max = 255)
        String street,

        @Size(max = 255)
        String complement,

        @NotBlank(message = "City is required")
        @Size(max = 100)
        String city,

        @NotBlank(message = "State is required")
        @Size(max = 100)
        String state,

        @NotBlank(message = "Zipcode is required")
        @Size(max = 20)
        String zipcode,

        @NotBlank(message = "Country is required")
        @Size(max = 100)
        String country,

        @NotBlank(message = "Phone is required")
        @Size(max = 100)
        String phone,

        @NotNull
        UUID userId,

        @Enumerated(EnumType.STRING)
        @NotNull
        AddressEntity.ResidenceType residenceType
) {
    public AddressUpdateRequest {
        if (residenceType == null) {
            residenceType = AddressEntity.ResidenceType.HOUSE;
        }
    }
}