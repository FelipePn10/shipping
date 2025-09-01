package redirex.shipping.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import redirex.shipping.dto.response.UserCouponResponse;
import redirex.shipping.entity.UserCouponEntity;

@Mapper(componentModel = "spring", uses = {CouponMapper.class})
public interface UserCouponMapper {
    // Mapeia o ID da carteira do usuário (UserWalletEntity.walletId) para userId no DTO
    @Mapping(source = "user.wallet.walletId", target = "userId")
    UserCouponResponse toDTO(UserCouponEntity entity);

    @Mapping(target = "user", ignore = true)
    UserCouponEntity toEntity(UserCouponResponse dto);
}