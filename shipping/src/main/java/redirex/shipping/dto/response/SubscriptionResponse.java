package redirex.shipping.dto.response;

import redirex.shipping.enums.SubscriptionPlanEnum;
import redirex.shipping.enums.SubscriptionStatusEnum;
import java.time.LocalDateTime;
import java.util.UUID;

public record SubscriptionResponse (
    UUID id,
    UUID userId,
    String stripeSubscriptionId,
    SubscriptionPlanEnum planType,
    SubscriptionStatusEnum status,
    LocalDateTime createdAt,
    LocalDateTime updatedAt,
    LocalDateTime cancelledAt
) {

}