package redirex.shipping.dto.response;

import redirex.shipping.enums.NotificationTypeEnum;
import java.time.LocalDateTime;
import java.util.UUID;

public record NotificationResponse (
    UUID id,
    UUID userId,
    UUID adminId,
    String title,
    String message,
    Boolean isRead,
    LocalDateTime createdAt,
    NotificationTypeEnum type
) {

}