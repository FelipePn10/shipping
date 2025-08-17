package redirex.shipping.dto.request;

import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Data;
import redirex.shipping.enums.NotificationTypeEnum;

import java.util.UUID;

@Data
@Builder
public class CreateNotificationRequest {
    @NotNull(message = "User ID is required")
    private UUID userId;

    @NotBlank(message = "Title is required")
    @Size(max = 100, message = "Title must not exceed 100 characters")
    private String title;

    @NotBlank(message = "Message is required")
    @Size(max = 1000, message = "Message must not exceed 1000 characters")
    private String message;

    @NotNull(message = "Notification type is required")
    private NotificationTypeEnum type;
}