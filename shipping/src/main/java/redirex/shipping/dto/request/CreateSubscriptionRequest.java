package redirex.shipping.dto.request;


import lombok.Data;
import redirex.shipping.enums.SubscriptionPlanEnum;

// Define o formato dos dados enviados pelo cliente para criar uma assinatura.
@Data
public class CreateSubscriptionRequest {
    private Long userId;
    private SubscriptionPlanEnum planType;
    private String paymentMethodId;
}
