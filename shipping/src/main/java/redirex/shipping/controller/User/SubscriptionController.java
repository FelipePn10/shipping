package redirex.shipping.controller.User;


import com.stripe.exception.StripeException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import redirex.shipping.dto.request.CreateSubscriptionRequest;
import redirex.shipping.dto.response.SubscriptionResponse;
import redirex.shipping.service.SubscriptionService;

@RestController
@RequestMapping("/api/v1/user/subscriptions")
public class SubscriptionController {
    private final SubscriptionService subscriptionService;
    public SubscriptionController(SubscriptionService subscriptionService) {
        this.subscriptionService = subscriptionService;
    }

    @PostMapping
    public ResponseEntity<SubscriptionResponse> createSubscription(@RequestBody CreateSubscriptionRequest request) {
        SubscriptionResponse response = subscriptionService.createSubscription(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<SubscriptionResponse> getSubscriptionByUserId(@PathVariable Long userId) {
        SubscriptionResponse response = subscriptionService.getSubscriptionByUserId(userId);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{userId")
    public ResponseEntity<SubscriptionResponse> cancelSubscriptionByUserId(@PathVariable Long userId) {
        SubscriptionResponse response = subscriptionService.cancelSubscriptionByUserId(userId);
        return ResponseEntity.ok(response);
    }
}
