package redirex.shipping.dto.request;

import jakarta.validation.constraints.*;
import redirex.shipping.enums.NotificationTypeEnum;

import java.util.UUID;

public record CreateNotificationRequest (
    @NotNull(message = "User ID is required")
    UUID userId,

    @NotBlank(message = "Title is required")
    @Size(max = 100, message = "Title must not exceed 100 characters")
    String title,

    @NotBlank(message = "Message is required")
    @Size(max = 1000, message = "Message must not exceed 1000 characters")
    String message,

    @NotNull(message = "Notification type is required")
    NotificationTypeEnum type
) {

        }