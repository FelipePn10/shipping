package redirex.shipping.controller.dto.response;

import lombok.Builder;
import lombok.Data;
import redirex.shipping.enums.NotificationTypeEnum;

import java.time.LocalDateTime;

@Data
@Builder
public class NotificationResponse {
    private Long id;
    private Long userId;
    private Long adminId;
    private String title;
    private String message;
    private Boolean isRead;
    private LocalDateTime createdAt;
    private NotificationTypeEnum type;
}