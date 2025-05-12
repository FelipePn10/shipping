package redirex.shipping.controller.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AddressResponse {
    private Long id;
    private String street;
    private String city;
    private String state;
    private String postalCode;
    private String country;
}