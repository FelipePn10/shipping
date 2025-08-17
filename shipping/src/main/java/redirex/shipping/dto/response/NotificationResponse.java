package redirex.shipping.dto.response;

import lombok.Builder;
import lombok.Data;
import redirex.shipping.enums.NotificationTypeEnum;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class NotificationResponse {
    private UUID id;
    private UUID userId;
    private UUID adminId;
    private String title;
    private String message;
    private Boolean isRead;
    private LocalDateTime createdAt;
    private NotificationTypeEnum type;
}