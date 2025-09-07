package redirex.shipping.dto.response;

import redirex.shipping.dto.request.CouponRequest;
import java.time.LocalDateTime;
import java.util.UUID;

public record UserCouponDTO (
    UUID id,
    UUID userId,
    CouponRequest coupon,
    Boolean isUsed,
    LocalDateTime usedAt,
    LocalDateTime assignedAt
) {

}