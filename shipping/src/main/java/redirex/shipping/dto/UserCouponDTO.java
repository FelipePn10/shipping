package redirex.shipping.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserCouponDTO {
    private Long id;
    private Long userId;
    private CouponDTO coupon;
    private Boolean isUsed;
    private LocalDateTime usedAt;
    private LocalDateTime assignedAt;
}