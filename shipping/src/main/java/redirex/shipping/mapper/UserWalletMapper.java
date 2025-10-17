package redirex.shipping.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import redirex.shipping.dto.request.UserWalletRequest;
import redirex.shipping.entity.UserWalletEntity;

@Mapper(componentModel = "spring")
public interface UserWalletMapper {

    @Mapping(source = "walletId", target = "id")
    @Mapping(source = "userId.id", target = "userId")
    @Mapping(source = "walletId", target = "walletId")
    @Mapping(source = "currency", target = "currency")
    @Mapping(source = "balance", target = "balance")
    @Mapping(source = "createdAt", target = "createdAt")
    UserWalletRequest toDTO(UserWalletEntity entity);
}