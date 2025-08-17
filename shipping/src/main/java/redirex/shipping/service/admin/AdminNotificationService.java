package redirex.shipping.service.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import redirex.shipping.entity.AdminEntity;
import redirex.shipping.entity.NotificationEntity;
import redirex.shipping.entity.UserEntity;
import redirex.shipping.enums.NotificationTypeEnum;
import redirex.shipping.repositories.AdminRepository;
import redirex.shipping.repositories.NotificationRepository;
import redirex.shipping.repositories.UserRepository;

import java.util.UUID;

@Service
public class AdminNotificationService {
    @Autowired
    private NotificationRepository notificationRepository;
    @Autowired
    private AdminRepository adminRepository;
    @Autowired
    private UserRepository userRepository;

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public NotificationEntity createNotification(UUID adminId, UUID userId, String title, String message, NotificationTypeEnum type) {
        AdminEntity admin = adminRepository.findById(adminId)
                .orElseThrow(() -> new IllegalArgumentException("Admin not found"));
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        NotificationEntity notification = NotificationEntity.builder()
                .admin(admin)
                .user(user)
                .title(title)
                .message(message)
                .type(type)
                .isRead(false)
                .build();

        return notificationRepository.save(notification);
    }
}
