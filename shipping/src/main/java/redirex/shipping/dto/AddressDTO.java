package redirex.shipping.dto;

import lombok.Data;
import redirex.shipping.entity.AddressEntity.ResidenceType;

import java.util.UUID;

@Data
public class AddressDTO {

    private UUID id;
    private String recipientName;
    private String street;
    private String complement;
    private String city;
    private String state;
    private String zipcode;
    private String country;
    private String phone;
    private ResidenceType residenceType;
    private UUID userId;
}