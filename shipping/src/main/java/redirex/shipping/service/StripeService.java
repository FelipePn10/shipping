package redirex.shipping.service;

import redirex.shipping.enums.CurrencyEnum;
import redirex.shipping.exception.StripePaymentException;

public interface StripeService {
    boolean processPayment(String paymentMethodId, Long amountInCents, CurrencyEnum currency) throws StripePaymentException;
}
