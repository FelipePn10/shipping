package redirex.shipping.dto.response;

import lombok.Builder;
import lombok.Data;
import redirex.shipping.entity.AddressEntity;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class AddressResponse {
        private UUID id;
        private UUID userId;
        private String recipientName;
        private String street;
        private String complement;
        private String city;
        private String state;
        private String zipcode;
        private String country;
        private String phone;
        private AddressEntity.ResidenceType residenceType;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
    }