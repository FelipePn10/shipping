package redirex.shipping.service;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import redirex.shipping.enums.CurrencyEnum;
import redirex.shipping.exception.StripePaymentException;

import jakarta.annotation.PostConstruct;

@Service
public class StripeServiceImpl implements StripeService {

    private static final Logger logger = LoggerFactory.getLogger(StripeServiceImpl.class);

    @Value("${stripe.api.secretKey}")
    private String secretKey;

    @PostConstruct
    public void init() {
        Stripe.apiKey = secretKey;
    }

    @Override
    public boolean processPayment(String paymentMethodId, Long amountInCents, CurrencyEnum currency) throws StripePaymentException {
        logger.info("Attempting to process Stripe payment. PaymentMethodID: {}, Amount: {} {}, Currency: {}",
                paymentMethodId, amountInCents, currency.name().toLowerCase(), currency.name());

        try {
            PaymentIntentCreateParams params =
                    PaymentIntentCreateParams.builder()
                            .setAmount(amountInCents)
                            .setCurrency(currency.name().toLowerCase())
                            .setPaymentMethod(paymentMethodId)
                            .setConfirmationMethod(PaymentIntentCreateParams.ConfirmationMethod.MANUAL)
                            .setConfirm(true) // Confirma o PaymentIntent imediatamente
                            .build();

            PaymentIntent paymentIntent = PaymentIntent.create(params);

            logger.info("PaymentIntent created with ID: {} and status: {}", paymentIntent.getId(), paymentIntent.getStatus());

            if ("succeeded".equals(paymentIntent.getStatus())) {
                logger.info("Payment successful for PaymentIntent ID: {}", paymentIntent.getId());
                return true;
            } else if ("requires_payment_method".equals(paymentIntent.getStatus())) {
                logger.warn("Payment failed for PaymentIntent ID: {}. Status: {}. Reason: Payment method declined or invalid.",
                        paymentIntent.getId(), paymentIntent.getStatus());
                throw new StripePaymentException("Payment failed: " + paymentIntent.getLastPaymentError().getMessage());
            } else if ("requires_action".equals(paymentIntent.getStatus())) {
                logger.warn("Payment requires action for PaymentIntent ID: {}. Status: {}", paymentIntent.getId(), paymentIntent.getStatus());
                throw new StripePaymentException("Payment requires further action (e.g., 3D Secure). This flow is not fully handled on the backend alone.");
            } else {
                logger.error("PaymentIntent ID: {} has unhandled status: {}. Full object: {}",
                        paymentIntent.getId(), paymentIntent.getStatus(), paymentIntent.toJson());
                throw new StripePaymentException("Payment processing failed with status: " + paymentIntent.getStatus());
            }

        } catch (StripeException e) {
            logger.error("Stripe API error during payment processing for PaymentMethodID: {}: {}", paymentMethodId, e.getMessage(), e);
            throw new StripePaymentException("Stripe API error: " + e.getMessage(), e);
        } catch (Exception e) {
            logger.error("Unexpected error during payment processing for PaymentMethodID: {}: {}", paymentMethodId, e.getMessage(), e);
            throw new StripePaymentException("Unexpected error during payment: " + e.getMessage(), e);
        }
    }
}
