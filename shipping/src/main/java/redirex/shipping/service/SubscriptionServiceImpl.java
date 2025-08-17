package redirex.shipping.service;



import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Subscription;
import com.stripe.model.Customer;
import com.stripe.model.PaymentMethod;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import redirex.shipping.dto.request.CreateSubscriptionRequest;
import redirex.shipping.dto.response.SubscriptionResponse;
import redirex.shipping.entity.SubscriptionEntity;
import redirex.shipping.entity.UserEntity;
import redirex.shipping.enums.SubscriptionPlanEnum;
import redirex.shipping.enums.SubscriptionStatusEnum;
import redirex.shipping.exception.StripePaymentException;
import redirex.shipping.repositories.SubscriptionRepository;
import redirex.shipping.repositories.UserRepository;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class SubscriptionServiceImpl implements SubscriptionService {
    private final SubscriptionRepository subscriptionRepository;
    private final UserRepository userRepository;
    private final String stripeApiKey;

    public SubscriptionServiceImpl(SubscriptionRepository subscriptionRepository,
                                   UserRepository userRepository,
                                   @Value("${stripe.api.key}") String stripeApiKey) {
        this.subscriptionRepository = subscriptionRepository;
        this.userRepository = userRepository;
        this.stripeApiKey = stripeApiKey;
        Stripe.apiKey = stripeApiKey;
    }

    @Override
    public SubscriptionResponse createSubscription(CreateSubscriptionRequest request) {
        UserEntity userEntity = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        try {
            // Criar ou recuperar cliente no Stripe
            Customer customer = getOrCreateStripeCustomer(userEntity);

            // Vincular método de pagamento do cliente
            PaymentMethod paymentMethod = PaymentMethod.retrieve(request.getPaymentMethodId());
            paymentMethod.attach(new  HashMap<String, Object>() {{
                put("customer", customer.getId());
            }});

            // Definir plano (mapeamento de SubscriptionPlanEnum para Stripe Price ID)
            String priceId = getStripePriceId(request.getPlanType());

            // Criar assinatura Stripe
            Map<String, Object> subscriptionParams = new HashMap<>();
            subscriptionParams.put("customer", customer.getId());
            subscriptionParams.put("items", new HashMap<String, Object>() {{
                put("0", new HashMap<String, Object>() {{
                    put("price", priceId);
                }});
            }});
            subscriptionParams.put("default_payment_method", request.getPaymentMethodId());
            Subscription stripeSubscription = Subscription.create(subscriptionParams);

            // Salvar assinatura no banco
            SubscriptionEntity subscriptionEntity = new SubscriptionEntity();
            subscriptionEntity.setUser(userEntity);
            subscriptionEntity.setStripeSubscriptionId(stripeSubscription.getId());
            subscriptionEntity.setPlanId(priceId);
            subscriptionEntity.setPlanType(request.getPlanType());
            subscriptionEntity.setStatus(SubscriptionStatusEnum.ACTIVE);
            subscriptionRepository.save(subscriptionEntity);

            return mapToResponse(subscriptionEntity);
        } catch (StripePaymentException | StripeException e) {
            throw new StripePaymentException("Error generating signature: " + e.getMessage());
        }
    }

    @Override
    public SubscriptionResponse getSubscriptionByUserId(UUID userId) {
        SubscriptionEntity subscriptionEntity = subscriptionRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Signature not found"));
        return mapToResponse(subscriptionEntity);
    }

    @Override
    public SubscriptionResponse cancelSubscriptionByUserId(UUID userId) {
        SubscriptionEntity subscription = subscriptionRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Signature not found"));
        try {
            Subscription stripeSubscription = Subscription.retrieve(subscription.getStripeSubscriptionId());
            stripeSubscription.cancel();

            subscription.setStatus(SubscriptionStatusEnum.CANCELLED);
            subscription.setCancelledAt(LocalDateTime.now());
            subscriptionRepository.save(subscription);

            return mapToResponse(subscription);
        } catch (StripeException e) {
            throw new StripePaymentException("Error generating signature: " + e.getMessage());
        }
    }

    private Customer getOrCreateStripeCustomer(UserEntity userEntity) throws StripeException {
        // Verificar se o cliente já existe no Stripe
        Map<String, Object> customerParams = new HashMap<>();
        customerParams.put("email", userEntity.getEmail());
        customerParams.put("name", userEntity.getFullname());
        return Customer.create(customerParams);
    }

    private String getStripePriceId(SubscriptionPlanEnum planType) {
        // Mapear planos para Prices IDs do Stripe (definido no Stripe Dashboard)
        return switch (planType) {
            case BASIC -> "price_basic_plan"; // Terá o ID real futuramente
            case PREMIUM ->  "price_premium_plan"; // // Terá o ID real futuramente
        };
    }

    private SubscriptionResponse mapToResponse(SubscriptionEntity entity) {
        SubscriptionResponse response = new SubscriptionResponse();
        response.setId(entity.getId());
        response.setUserId(entity.getUser().getId());
        response.setStripeSubscriptionId(entity.getStripeSubscriptionId());
        response.setPlanType(entity.getPlanType());
        response.setStatus(entity.getStatus());
        response.setCreatedAt(entity.getCreatedAt());
        response.setUpdatedAt(entity.getUpdatedAt());
        response.setCancelledAt(entity.getCancelledAt());
        return response;
    }
}
