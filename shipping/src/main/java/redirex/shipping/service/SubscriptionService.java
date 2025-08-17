package redirex.shipping.service;

import redirex.shipping.dto.request.CreateSubscriptionRequest;
import redirex.shipping.dto.response.SubscriptionResponse;

import java.util.UUID;

public interface SubscriptionService {
    SubscriptionResponse createSubscription(CreateSubscriptionRequest request);
    SubscriptionResponse getSubscriptionByUserId(UUID userId);
    SubscriptionResponse cancelSubscriptionByUserId(UUID userId);

}