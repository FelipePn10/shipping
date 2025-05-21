package redirex.shipping.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class AddressResponse {
    private Long id;
    private String street;
    private String city;
    private String state;
    private String zipcode;
    private String country;
    private String recipientName;
    private String phone;
    private LocalDateTime createdAt;
}