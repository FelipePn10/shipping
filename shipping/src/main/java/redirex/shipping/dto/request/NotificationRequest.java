package redirex.shipping.dto.request;

import redirex.shipping.enums.NotificationTypeEnum;


import java.time.LocalDateTime;
import java.util.UUID;

public record NotificationRequest (
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