package redirex.shipping.mapper;

import org.mapstruct.Mapper;
import redirex.shipping.dto.CouponDTO;
import redirex.shipping.entity.CouponEntity;

@Mapper(componentModel = "spring")
public interface CouponMapper {
    CouponDTO toDTO(CouponEntity entity);
    CouponEntity toEntity(CouponDTO dto);
}