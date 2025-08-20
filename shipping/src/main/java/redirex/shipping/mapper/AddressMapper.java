package redirex.shipping.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import redirex.shipping.dto.request.AddressRequest;
import redirex.shipping.dto.request.AddressUpdateRequest;
import redirex.shipping.dto.request.CreateAddressRequest;
import redirex.shipping.dto.response.AddressResponse;
import redirex.shipping.entity.AddressEntity;

@Mapper(componentModel = "spring")
public interface AddressMapper {
    AddressResponse toResponse(AddressEntity entity);
    AddressUpdateRequest toUpdateRequest(AddressRequest dto);
    AddressRequest toDTO(CreateAddressRequest request);
    AddressRequest toDTO(AddressUpdateRequest request);
    AddressEntity toEntity(AddressRequest dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDto(AddressRequest dto, @MappingTarget AddressEntity entity);
}