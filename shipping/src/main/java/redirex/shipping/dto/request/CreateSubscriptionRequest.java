package redirex.shipping.dto.request;
import redirex.shipping.enums.SubscriptionPlanEnum;

import java.util.UUID;

public record CreateSubscriptionRequest (
    UUID userId,
    SubscriptionPlanEnum planType,
    String paymentMethodId
){

}
