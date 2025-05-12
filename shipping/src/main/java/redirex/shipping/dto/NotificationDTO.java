package redirex.shipping.dto;

import lombok.Data;
import redirex.shipping.enums.NotificationTypeEnum;


import java.time.LocalDateTime;

@Data
public class NotificationDTO {
    private Long id;
    private Long userId;
    private Long adminId;
    private String title;
    private String message;
    private Boolean isRead;
    private LocalDateTime createdAt;
    private NotificationTypeEnum type;
}