package redirex.shipping.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import redirex.shipping.entity.CouponEntity;
import redirex.shipping.entity.UserCouponEntity;
import redirex.shipping.entity.UserEntity;
import redirex.shipping.enums.CouponTypeEnum;
import redirex.shipping.repositories.CouponRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class CouponService {
    private static final Logger logger = LoggerFactory.getLogger(CouponService.class);

    private final CouponRepository couponRepository;

    @Transactional
    public UserCouponEntity createWelcomeCoupon(UserEntity user) {
        logger.info("Creating welcome coupon for user: {}", user.getEmail());

        // Cria o cupom de boas-vindas
        CouponEntity coupon = CouponEntity.builder()
                .code("WELCOME_" + user.getId())
                .description("Welcome coupon: 2.5% off shipping")
                .discountPercentage(2.5)
                .maxDiscountValue(BigDecimal.valueOf(250.00)) // Limite máximo de desconto
                .validFrom(LocalDateTime.now())
                .validTo(LocalDateTime.now().plusDays(30))
                .type(CouponTypeEnum.SHIPPING)
                .minPurchaseValue(BigDecimal.ZERO)
                .isActive(true)
                .isWelcomeCoupon(true)
                .isNewsletterCoupon(false)
                .build();
        coupon = couponRepository.save(coupon);

        // Associa o cupom ao usuário
        UserCouponEntity userCoupon = UserCouponEntity.builder()
                .user(user)
                .coupon(coupon)
                .isUsed(false)
                .assignedAt(LocalDateTime.now())
                .build();
        logger.info("Welcome coupon created and assigned to user: {}", user.getEmail());
        return userCoupon;
    }
}
