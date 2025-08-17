package redirex.shipping.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import redirex.shipping.entity.AdminEntity;
import redirex.shipping.entity.NotificationEntity;
import redirex.shipping.entity.UserEntity;
import redirex.shipping.enums.NotificationTypeEnum;

import java.util.List;
import java.util.UUID;

public interface NotificationRepository extends JpaRepository<NotificationEntity, UUID> {
    List<NotificationEntity> findByUser(UserEntity user);
    List<NotificationEntity> findByUserAndIsReadFalse(UserEntity user);
    List<NotificationEntity> findByType(NotificationTypeEnum type);
    List<NotificationEntity> findByAdmin(AdminEntity admin);
}