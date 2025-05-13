package redirex.shipping.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import redirex.shipping.controller.dto.response.UserResponse;
import redirex.shipping.dto.UserDTO;
import redirex.shipping.entity.UserEntity;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserDTO toDTO(UserEntity entity);

    @Mapping(target = "password", ignore = true)
    UserEntity toEntity(UserDTO dto);

    @Mapping(source = "fullname", target = "name")
    UserResponse toResponse(UserEntity entity);
}