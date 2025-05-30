package redirex.shipping.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import redirex.shipping.dto.UserWalletDTO;
import redirex.shipping.entity.UserWalletEntity;

@Mapper(componentModel = "spring")
public interface UserWalletMapper {

    // Mapeia o ID do usuário (UserEntity.id) para um campo no DTO
    @Mapping(source = "userId.id", target = "userId")
    // Mapeia o ID da carteira (UserWalletEntity.walletId)
    @Mapping(source = "walletId", target = "walletId")
    UserWalletDTO toDTO(UserWalletEntity entity);
}