package redirex.shipping.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import redirex.shipping.dto.request.AddressRequest;
import redirex.shipping.dto.request.AddressUpdateRequest;
import redirex.shipping.dto.request.CreateAddressRequest;
import redirex.shipping.dto.response.AddressResponse;
import redirex.shipping.entity.AddressEntity;

@Mapper(componentModel = "spring")
public interface AddressMapper {

    @Mapping(source = "user.id", target = "userId")
    AddressResponse toResponse(AddressEntity entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "user", ignore = true)
    AddressEntity toEntity(AddressRequest dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "user", ignore = true)
    AddressEntity toEntity(CreateAddressRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "user", ignore = true)
    void updateEntityFromDto(AddressRequest dto, @MappingTarget AddressEntity entity);

    // Converte CreateAddressRequest -> AddressRequest
    AddressRequest toDTO(CreateAddressRequest request);
}