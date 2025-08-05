package redirex.shipping.service;

import redirex.shipping.dto.request.CreateSubscriptionRequest;
import redirex.shipping.dto.response.SubscriptionResponse;

public interface SubscriptionService {
    SubscriptionResponse createSubscription(CreateSubscriptionRequest request);
    SubscriptionResponse getSubscriptionByUserId(Long userId);
    SubscriptionResponse cancelSubscriptionByUserId(Long userId);

}