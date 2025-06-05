package redirex.shipping.service;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import redirex.shipping.enums.CurrencyEnum;
import redirex.shipping.exception.StripePaymentException;

import java.util.HashMap;
import java.util.Map;

@Service
@Profile("test")
public class MockStripeServiceTest implements StripeService {

    // Armazena estados de pagamento para testes
    private final Map<String, Boolean> paymentStates = new HashMap<>();

    public void setPaymentResult(String paymentMethodId, boolean success) {
        paymentStates.put(paymentMethodId, success);
    }

    @Override
    public boolean processPayment(String paymentMethodId, Long amountInCents, CurrencyEnum currency) {
        if ("pm_error".equals(paymentMethodId)) {
            throw new StripePaymentException("Simulated payment error");
        }

        if ("pm_insufficient_funds".equals(paymentMethodId)) {
            return false;
        }

        // Retorna sucesso por padrão ou conforme configurado
        return paymentStates.getOrDefault(paymentMethodId, true);
    }
}