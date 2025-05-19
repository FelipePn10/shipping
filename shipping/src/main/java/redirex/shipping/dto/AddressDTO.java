package redirex.shipping.dto;

import lombok.Data;
import redirex.shipping.entity.AddressEntity.ResidenceType;

@Data
public class AddressDTO {

    private Long id;
    private String recipientName;
    private String street;
    private String complement;
    private String city;
    private String state;
    private String zipcode;
    private String country;
    private String phone;
    private ResidenceType residenceType;
    private Long userId;
}