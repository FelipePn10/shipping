package redirex.shipping.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class UserCouponDTO {
    private UUID id;
    private UUID userId;
    private CouponDTO coupon;
    private Boolean isUsed;
    private LocalDateTime usedAt;
    private LocalDateTime assignedAt;
}