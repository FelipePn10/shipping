package redirex.shipping.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import redirex.shipping.dto.UserCouponDTO;
import redirex.shipping.entity.UserCouponEntity;

@Mapper(componentModel = "spring", uses = {CouponMapper.class})
public interface UserCouponMapper {
    @Mapping(source = "user.walletId", target = "userId")
    UserCouponDTO toDTO(UserCouponEntity entity);

    @Mapping(target = "user", ignore = true)
    UserCouponEntity toEntity(UserCouponDTO dto);
}