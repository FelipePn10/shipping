package redirex.shipping.dto;

import lombok.Data;
import redirex.shipping.enums.NotificationTypeEnum;


import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class NotificationDTO {
    private UUID id;
    private UUID userId;
    private UUID adminId;
    private String title;
    private String message;
    private Boolean isRead;
    private LocalDateTime createdAt;
    private NotificationTypeEnum type;
}