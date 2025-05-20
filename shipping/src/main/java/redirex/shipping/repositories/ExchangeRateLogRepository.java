package redirex.shipping.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import redirex.shipping.entity.ExchangeRateLogEntity;

public interface ExchangeRateLogRepository extends JpaRepository<ExchangeRateLogEntity, Long> {
}