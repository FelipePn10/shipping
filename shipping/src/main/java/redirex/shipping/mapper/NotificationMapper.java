package redirex.shipping.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import redirex.shipping.dto.NotificationDTO;
import redirex.shipping.entity.NotificationEntity;

@Mapper(componentModel = "spring")
public interface NotificationMapper {
    @Mapping(source = "user.walletId", target = "userId")
    @Mapping(source = "admin.walletId", target = "adminId")
    NotificationDTO toDTO(NotificationEntity entity);

    @Mapping(target = "user", ignore = true)
    @Mapping(target = "admin", ignore = true)
    NotificationEntity toEntity(NotificationDTO dto);
}