package redirex.shipping.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import redirex.shipping.dto.AdminDTO;
import redirex.shipping.entity.AdminEntity;

@Mapper(componentModel = "spring")
public interface AdminMapper {
    AdminDTO toDTO(AdminEntity entity);

    @Mapping(target = "password", ignore = true)
    AdminEntity toEntity(AdminDTO dto);
}