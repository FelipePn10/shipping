package redirex.shipping.mapper;

import java.time.LocalDateTime;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;
import redirex.shipping.dto.request.RegisterAdminRequest;
import redirex.shipping.dto.request.UpdateAdminRequest;
import redirex.shipping.dto.response.AuthAdminResponse;
import redirex.shipping.dto.response.RegisterAdminResponse;
import redirex.shipping.dto.response.UpdateAdminResponse;
import redirex.shipping.entity.AdminEntity;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    comments = "version: 1.6.3, compiler: javac, environment: Java 21.0.8 (Arch Linux)"
)
@Component
public class AdminMapperImpl implements AdminMapper {

    @Override
    public AdminEntity toEntity(RegisterAdminRequest request) {
        if ( request == null ) {
            return null;
        }

        AdminEntity.AdminEntityBuilder adminEntity = AdminEntity.builder();

        adminEntity.fullname( request.fullname() );
        adminEntity.email( request.email() );
        adminEntity.password( request.password() );
        adminEntity.cpf( request.cpf() );
        adminEntity.role( request.role() );

        adminEntity.createdAt( java.time.LocalDateTime.now() );

        return adminEntity.build();
    }

    @Override
    public AdminEntity toEntity(UpdateAdminRequest request) {
        if ( request == null ) {
            return null;
        }

        AdminEntity.AdminEntityBuilder adminEntity = AdminEntity.builder();

        adminEntity.fullname( request.fullname() );
        adminEntity.email( request.email() );
        adminEntity.password( request.password() );

        return adminEntity.build();
    }

    @Override
    public RegisterAdminResponse toRegisterResponse(AdminEntity entity) {
        if ( entity == null ) {
            return null;
        }

        String fullname = null;
        String email = null;
        String cpf = null;
        String role = null;
        LocalDateTime createdAt = null;

        fullname = entity.getFullname();
        email = entity.getEmail();
        cpf = entity.getCpf();
        role = entity.getRole();
        createdAt = entity.getCreatedAt();

        RegisterAdminResponse registerAdminResponse = new RegisterAdminResponse( fullname, email, cpf, role, createdAt );

        return registerAdminResponse;
    }

    @Override
    public UpdateAdminResponse toUpdateResponse(AdminEntity entity) {
        if ( entity == null ) {
            return null;
        }

        String fullname = null;
        String email = null;

        fullname = entity.getFullname();
        email = entity.getEmail();

        UpdateAdminResponse updateAdminResponse = new UpdateAdminResponse( fullname, email );

        return updateAdminResponse;
    }

    @Override
    public AuthAdminResponse toAuthResponse(AdminEntity entity) {
        if ( entity == null ) {
            return null;
        }

        String fullname = null;
        String email = null;

        fullname = entity.getFullname();
        email = entity.getEmail();

        String token = null;

        AuthAdminResponse authAdminResponse = new AuthAdminResponse( fullname, email, token );

        return authAdminResponse;
    }
}
