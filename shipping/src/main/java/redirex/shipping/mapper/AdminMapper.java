package redirex.shipping.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import redirex.shipping.dto.request.RegisterAdminRequest;
import redirex.shipping.dto.request.UpdateAdminRequest;
import redirex.shipping.dto.response.AuthAdminResponse;
import redirex.shipping.dto.response.RegisterAdminResponse;
import redirex.shipping.dto.response.UpdateAdminResponse;
import redirex.shipping.entity.AdminEntity;

@Mapper(componentModel = "spring")
public interface AdminMapper {

    // Request -> Entity
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())")
    AdminEntity toEntity(RegisterAdminRequest request);

    @Mapping(target = "id", ignore = true)
    AdminEntity toEntity(UpdateAdminRequest request);

    // Entity -> Response específico
    RegisterAdminResponse toRegisterResponse(AdminEntity entity);

    UpdateAdminResponse toUpdateResponse(AdminEntity entity);

    AuthAdminResponse toAuthResponse(AdminEntity entity);
}