package redirex.shipping.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import redirex.shipping.dto.NotificationDTO;
import redirex.shipping.entity.NotificationEntity;

@Mapper(componentModel = "spring")
public interface NotificationMapper {
    // Mapeia o ID da carteira do usuário (UserWalletEntity.walletId) para userId no DTO
    @Mapping(source = "user.wallet.walletId", target = "userId")
    // Mapeia o ID da carteira do admin (UserWalletEntity.walletId) para adminId no DTO
    @Mapping(source = "admin.wallet.walletId", target = "adminId")
    NotificationDTO toDTO(NotificationEntity entity);

    @Mapping(target = "user", ignore = true)
    @Mapping(target = "admin", ignore = true)
    NotificationEntity toEntity(NotificationDTO dto);
}