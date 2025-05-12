package redirex.shipping.mapper;

import org.mapstruct.Mapper;
import redirex.shipping.controller.dto.response.AddressResponse;
import redirex.shipping.entity.AddressEntity;

@Mapper(componentModel = "spring")
public interface AddressMapper {
    AddressResponse toResponse(AddressEntity entity);
    AddressEntity toEntity(AddressResponse response);
}