package redirex.shipping.dto.response;

import lombok.Data;
import redirex.shipping.dto.request.CouponRequest;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class UserCouponDTO {
    private UUID id;
    private UUID userId;
    private CouponRequest coupon;
    private Boolean isUsed;
    private LocalDateTime usedAt;
    private LocalDateTime assignedAt;
}