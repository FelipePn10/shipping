package redirex.shipping.mapper;

import org.mapstruct.Mapper;
import redirex.shipping.controller.dto.request.CreateAddressRequest;
import redirex.shipping.controller.dto.response.AddressResponse;
import redirex.shipping.dto.AddressDTO;
import redirex.shipping.entity.AddressEntity;

@Mapper(componentModel = "spring")
public interface AddressMapper {
    AddressResponse toResponse(AddressEntity entity);
    AddressDTO toDTO(CreateAddressRequest request);
    AddressEntity toEntity(AddressDTO dto);
}