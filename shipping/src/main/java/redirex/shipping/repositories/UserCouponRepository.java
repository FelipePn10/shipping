package redirex.shipping.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import redirex.shipping.entity.CouponEntity;
import redirex.shipping.entity.UserCouponEntity;
import redirex.shipping.entity.UserEntity;

import java.util.List;

public interface UserCouponRepository extends JpaRepository<UserCouponEntity, Long> {
    List<UserCouponEntity> findByUserAndIsUsedFalse(UserEntity user);
    List<UserCouponEntity> findByCoupon(CouponEntity coupon);
}