package redirex.shipping.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import redirex.shipping.entity.CouponEntity;
import redirex.shipping.entity.UserCouponEntity;
import redirex.shipping.entity.UserEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserCouponRepository extends JpaRepository<UserCouponEntity, UUID> {
    List<UserCouponEntity> findByUserAndUsedFalse(UserEntity user);
    List<UserCouponEntity> findByCoupon(CouponEntity coupon);
    Optional<UserCouponEntity> findByCouponCodeAndUser(String couponCode, UserEntity user);
}