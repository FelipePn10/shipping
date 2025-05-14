package redirex.shipping.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import redirex.shipping.entity.UserCouponEntity;
import redirex.shipping.entity.UserEntity;
import redirex.shipping.enums.CurrencyEnum;
import redirex.shipping.util.CouponCodeGenerator;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class CouponService {

    private final CouponCodeGenerator couponCodeGenerator;

    @Transactional
    public UserCouponEntity createWelcomeCoupon(UserEntity user) {
        String couponCode = couponCodeGenerator.generateCode(12);
        return UserCouponEntity.builder()
                .user(user)
                .couponCode(couponCode)
                .discountPercentage(new BigDecimal("2.50"))
                .currency(CurrencyEnum.BRL)
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusDays(30))
                .used(false)
                .build();
    }

    @Transactional
    public UserCouponEntity createCustomCoupon(UserEntity user, BigDecimal discountPercentage, CurrencyEnum currency, LocalDateTime expiresAt) {
        String couponCode = couponCodeGenerator.generateCode(12);
        return UserCouponEntity.builder()
                .user(user)
                .couponCode(couponCode)
                .discountPercentage(discountPercentage)
                .currency(currency)
                .createdAt(LocalDateTime.now())
                .expiresAt(expiresAt)
                .used(false)
                .build();
    }
}