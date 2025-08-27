package redirex.shipping.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import redirex.shipping.dto.request.NotificationRequest;
import redirex.shipping.entity.NotificationEntity;

@Mapper(componentModel = "spring")
public interface NotificationMapper {
    // Mapeia o ID da carteira do usuário (UserWalletEntity.walletId) para userId no DTO
    @Mapping(source = "user.wallet.walletId", target = "userId")
    // Mapeia o ID do admin (AdminEntity.id) para adminId no DTO
    @Mapping(source = "admin.id", target = "adminId")
    NotificationRequest toDTO(NotificationEntity entity);

    @Mapping(target = "user", ignore = true)
    @Mapping(target = "admin", ignore = true)
    NotificationEntity toEntity(NotificationRequest dto);
}