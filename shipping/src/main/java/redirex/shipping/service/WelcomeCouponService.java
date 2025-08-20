package redirex.shipping.service;

import org.springframework.stereotype.Service;
import redirex.shipping.entity.CouponEntity;
import redirex.shipping.enums.CouponTypeEnum;
import redirex.shipping.repositories.CouponRepository;
import redirex.shipping.util.CouponCodeGenerator;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class WelcomeCouponService {

    private final CouponCodeGenerator couponCodeGenerator;
    private final CouponRepository couponRepository;

    public WelcomeCouponService(CouponCodeGenerator couponCodeGenerator, CouponRepository couponRepository) {
        this.couponCodeGenerator = couponCodeGenerator;
        this.couponRepository = couponRepository;
    }

    public CouponEntity createWelcomeCoupon() {
        String couponCode = couponCodeGenerator.generateCode(6);
        CouponEntity coupon = CouponEntity.builder()
                .code(couponCode)
                .isActive(true)
                .validFrom(LocalDateTime.now())
                .validTo(LocalDateTime.now().plusDays(30))
                .type(CouponTypeEnum.SHIPPING)
                .discountPercentage(new BigDecimal("2.50"))
                .isWelcomeCoupon(true)
                .isNewsletterCoupon(false)
                .build();
        return couponRepository.save(coupon); //  Apenas cria e retorna o cupom
    }
}