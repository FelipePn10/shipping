package redirex.shipping.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import redirex.shipping.dto.AdminDTO;
import redirex.shipping.dto.response.AdminResponse;
import redirex.shipping.dto.response.UserResponse;
import redirex.shipping.entity.AdminEntity;
import redirex.shipping.entity.UserEntity;

@Mapper(componentModel = "spring")
public interface AdminMapper {
    AdminDTO toDTO(AdminEntity entity);

    @Mapping(target = "password", ignore = true)
    AdminEntity toEntity(AdminDTO dto);

    AdminResponse toResponse(AdminEntity entity);

}


