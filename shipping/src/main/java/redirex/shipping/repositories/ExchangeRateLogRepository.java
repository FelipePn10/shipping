package redirex.shipping.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import redirex.shipping.entity.ExchangeRateLogEntity;

import java.util.UUID;

public interface ExchangeRateLogRepository extends JpaRepository<ExchangeRateLogEntity, UUID> {
}