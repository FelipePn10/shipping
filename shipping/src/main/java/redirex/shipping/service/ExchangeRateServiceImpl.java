package redirex.shipping.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import redirex.shipping.entity.ExchangeRateLogEntity;
import redirex.shipping.enums.CurrencyEnum;
import redirex.shipping.repositories.ExchangeRateLogRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class ExchangeRateServiceImpl implements ExchangeRateService {

    private final ExchangeRateLogRepository exchangeRateLogRepository;

    private static final BigDecimal BRL_TO_CNY_RATE = new BigDecimal("1.155");

    @Override
    @Cacheable(value = "exchangeRates", key = "#fromCurrency + '-' + #toCurrency")
    public BigDecimal getExchangeRate(CurrencyEnum fromCurrency, CurrencyEnum toCurrency) {
        if (fromCurrency == toCurrency) {
            return BigDecimal.ONE;
        }

        if (fromCurrency != CurrencyEnum.BRL || toCurrency != CurrencyEnum.CNY) {
            throw new IllegalArgumentException("Apenas a cotação de BRL para CNY é suportada");
        }

        BigDecimal rate = BRL_TO_CNY_RATE;
        logExchangeRate(fromCurrency, toCurrency, rate, "MANUAL");
        return rate;
    }

    @Override
    public void logExchangeRate(CurrencyEnum fromCurrency, CurrencyEnum toCurrency, BigDecimal rate, String source) {
        ExchangeRateLogEntity logEntity = ExchangeRateLogEntity.builder()
                .fromCurrency(fromCurrency)
                .toCurrency(toCurrency)
                .rate(rate)
                .source(source)
                .fetchedAt(LocalDateTime.now())
                .build();
        exchangeRateLogRepository.save(logEntity);
        log.info("Cotação salva: {} -> {} = {}, fonte: {}", fromCurrency, toCurrency, rate, source);
    }
}