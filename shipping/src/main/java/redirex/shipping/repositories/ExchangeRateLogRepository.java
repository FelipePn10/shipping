package redirex.shipping.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import redirex.shipping.entity.ExchangeRateLogEntity;
import redirex.shipping.enums.CurrencyEnum;


import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ExchangeRateLogRepository extends JpaRepository<ExchangeRateLogEntity, Long> {
    List<ExchangeRateLogEntity> findByFromCurrencyAndToCurrency(CurrencyEnum fromCurrency, CurrencyEnum toCurrency);
    Optional<ExchangeRateLogEntity> findTopByFromCurrencyAndToCurrencyOrderByFetchedAtDesc(CurrencyEnum fromCurrency, CurrencyEnum toCurrency);
    List<ExchangeRateLogEntity> findByFetchedAtBetween(LocalDateTime start, LocalDateTime end);
}