package redirex.shipping.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

public record UserCouponResponse (
    UUID id,
    UUID userId,
    CouponResponse coupon,
    Boolean isUsed,
    LocalDateTime usedAt,
    LocalDateTime assignedAt
) {

}