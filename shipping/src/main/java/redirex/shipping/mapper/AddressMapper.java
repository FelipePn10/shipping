package redirex.shipping.mapper;

import org.mapstruct.Mapper;
import redirex.shipping.dto.request.AddressUpdateRequest;
import redirex.shipping.dto.request.CreateAddressRequest;
import redirex.shipping.dto.response.AddressResponse;
import redirex.shipping.dto.AddressDTO;
import redirex.shipping.entity.AddressEntity;

@Mapper(componentModel = "spring")
public interface AddressMapper {
    AddressResponse toResponse(AddressEntity entity);
    AddressUpdateRequest toUpdateRequest(AddressDTO dto);
    AddressDTO toDTO(CreateAddressRequest request);
    AddressDTO toDTO(AddressUpdateRequest request);
    AddressEntity toEntity(AddressDTO dto);
}