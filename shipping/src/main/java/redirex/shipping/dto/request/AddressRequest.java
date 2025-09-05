package redirex.shipping.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import redirex.shipping.entity.AddressEntity;

public record AddressRequest(
        @NotBlank
        String recipientName,

        @NotBlank
        String street,

        String complement,

        @NotBlank
        String city,

        @NotBlank
        String state,

        @NotBlank
        @Size(min = 2, max = 16)
        String zipcode,

        @NotBlank
        String country,

        String phone,

        AddressEntity.ResidenceType residenceType
) {
        public AddressRequest {
                if (residenceType == null) {
                        residenceType = AddressEntity.ResidenceType.HOUSE;
                }
        }
}

    //*** Não é possível criar endereço, fazer a correção!