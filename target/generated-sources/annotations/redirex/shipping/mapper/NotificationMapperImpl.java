package redirex.shipping.mapper;

import java.time.LocalDateTime;
import java.util.UUID;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;
import redirex.shipping.dto.request.NotificationRequest;
import redirex.shipping.entity.AdminEntity;
import redirex.shipping.entity.NotificationEntity;
import redirex.shipping.entity.UserEntity;
import redirex.shipping.entity.UserWalletEntity;
import redirex.shipping.enums.NotificationTypeEnum;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    comments = "version: 1.6.3, compiler: javac, environment: Java 21.0.8 (Arch Linux)"
)
@Component
public class NotificationMapperImpl implements NotificationMapper {

    @Override
    public NotificationRequest toDTO(NotificationEntity entity) {
        if ( entity == null ) {
            return null;
        }

        UUID userId = null;
        UUID adminId = null;
        UUID id = null;
        String title = null;
        String message = null;
        Boolean isRead = null;
        LocalDateTime createdAt = null;
        NotificationTypeEnum type = null;

        userId = entityUserWalletWalletId( entity );
        adminId = entityAdminId( entity );
        id = entity.getId();
        title = entity.getTitle();
        message = entity.getMessage();
        isRead = entity.getIsRead();
        createdAt = entity.getCreatedAt();
        type = entity.getType();

        NotificationRequest notificationRequest = new NotificationRequest( id, userId, adminId, title, message, isRead, createdAt, type );

        return notificationRequest;
    }

    @Override
    public NotificationEntity toEntity(NotificationRequest dto) {
        if ( dto == null ) {
            return null;
        }

        NotificationEntity.NotificationEntityBuilder notificationEntity = NotificationEntity.builder();

        notificationEntity.id( dto.id() );
        notificationEntity.title( dto.title() );
        notificationEntity.message( dto.message() );
        notificationEntity.isRead( dto.isRead() );
        notificationEntity.createdAt( dto.createdAt() );
        notificationEntity.type( dto.type() );

        return notificationEntity.build();
    }

    private UUID entityUserWalletWalletId(NotificationEntity notificationEntity) {
        UserEntity user = notificationEntity.getUser();
        if ( user == null ) {
            return null;
        }
        UserWalletEntity wallet = user.getWallet();
        if ( wallet == null ) {
            return null;
        }
        return wallet.getWalletId();
    }

    private UUID entityAdminId(NotificationEntity notificationEntity) {
        AdminEntity admin = notificationEntity.getAdmin();
        if ( admin == null ) {
            return null;
        }
        return admin.getId();
    }
}
