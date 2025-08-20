package redirex.shipping.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import redirex.shipping.entity.AddressEntity.ResidenceType;

@Data
public class AddressRequest {

        @NotBlank
        private String recipientName;
        @NotBlank
        private String street;
        private String complement;
        @NotBlank
        private String city;
        @NotBlank
        private String state;
        @NotBlank
        @Size(min = 2, max = 16)
        private String zipcode;
        @NotBlank
        private String country;
        private String phone;
        private ResidenceType residenceType;
    }

    //*** Não é possível criar endereço, fazer a correção!