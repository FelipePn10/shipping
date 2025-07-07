package redirex.shipping.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import redirex.shipping.entity.SubscriptionEntity;

import java.util.Optional;

public interface SubscriptionRepository extends JpaRepository<SubscriptionEntity, Long> {
    Optional<SubscriptionEntity> findByUserId(Long userId);
    Optional<SubscriptionEntity> findByStripeSubscriptionId(String stripeSubscriptionId);
}