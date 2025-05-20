package redirex.shipping.service;

import redirex.shipping.enums.CurrencyEnum;
import java.math.BigDecimal;

public interface ExchangeRateService {

    BigDecimal getExchangeRate(CurrencyEnum fromCurrency, CurrencyEnum toCurrency);

    void logExchangeRate(CurrencyEnum fromCurrency, CurrencyEnum toCurrency, BigDecimal rate, String source);
}