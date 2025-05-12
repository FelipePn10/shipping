//package redirex.shipping.mapper;
//
//import org.mapstruct.Mapper;
//import redirex.shipping.dto.UserDTO;
//import redirex.shipping.entity.UserEntity;
//
//@Mapper(componentModel = "spring")
//public interface UserMapper {
//    UserDTO toDTO(UserEntity entity);
//
//    @Mapping(target = "password", ignore = true) // Assumindo que UserEntity tem password
//    UserEntity toEntity(UserDTO dto);
//}