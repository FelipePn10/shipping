package redirex.shipping.controller.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class UserCouponResponse {
    private Long id;
    private Long userId;
    private CouponResponse coupon;
    private Boolean isUsed;
    private LocalDateTime usedAt;
    private LocalDateTime assignedAt;
}