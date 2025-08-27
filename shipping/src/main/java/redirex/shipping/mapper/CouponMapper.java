package redirex.shipping.mapper;

import org.mapstruct.Mapper;
import redirex.shipping.dto.request.CouponRequest;
import redirex.shipping.entity.CouponEntity;

@Mapper(componentModel = "spring")
public interface CouponMapper {
    CouponRequest toDTO(CouponEntity entity);
    CouponEntity toEntity(CouponRequest dto);
}