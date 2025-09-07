package redirex.shipping.dto.response;

import redirex.shipping.entity.AddressEntity;

import java.time.LocalDateTime;
import java.util.UUID;

public record AddressResponse (
        UUID id,
        UUID userId,
        String recipientName,
        String street,
        String complement,
        String city,
        String state,
        String zipcode,
        String country,
        String phone,
        AddressEntity.ResidenceType residenceType,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
        ) {

}