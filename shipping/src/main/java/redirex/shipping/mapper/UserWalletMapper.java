package redirex.shipping.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import redirex.shipping.dto.UserWalletDTO;
import redirex.shipping.entity.UserWalletEntity;

@Mapper(componentModel = "spring")
public interface UserWalletMapper {
    @Mapping(source = "user.walletId", target = "userId")
    UserWalletDTO toDTO(UserWalletEntity entity);

    @Mapping(target = "user", ignore = true)
    UserWalletEntity toEntity(UserWalletDTO dto);
}