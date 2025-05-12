package redirex.shipping.mapper;

import org.mapstruct.Mapper;
import redirex.shipping.dto.AddressDTO;
import redirex.shipping.entity.AddressEntity;

@Mapper(componentModel = "spring")
public interface AddressMapper {
    AddressDTO toDTO(AddressEntity entity);
    AddressEntity toEntity(AddressDTO dto);
}