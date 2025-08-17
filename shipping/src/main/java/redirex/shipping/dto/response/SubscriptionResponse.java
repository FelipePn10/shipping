package redirex.shipping.dto.response;

import lombok.Data;
import redirex.shipping.enums.SubscriptionPlanEnum;
import redirex.shipping.enums.SubscriptionStatusEnum;

import java.time.LocalDateTime;
import java.util.UUID;

// Resposta enviada ao cliente após operações com assinaturas.
@Data
public class SubscriptionResponse {
    private UUID id;
    private UUID userId;
    private String stripeSubscriptionId;
    private SubscriptionPlanEnum planType;
    private SubscriptionStatusEnum status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime cancelledAt;
}