package redirex.shipping.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import redirex.shipping.dto.WalletTransactionDTO;
import redirex.shipping.dto.response.WalletTransactionResponse;
import redirex.shipping.entity.WalletTransactionEntity;

@Mapper(componentModel = "spring")
public interface WalletTransactionMapper {
    @Mapping(source = "userWallet.walletId", target = "userWalletId")
    WalletTransactionDTO toDTO(WalletTransactionEntity entity);

    @Mapping(target = "userWallet", ignore = true)
    WalletTransactionEntity toEntity(WalletTransactionDTO dto);

    @Mapping(source = "userWalletId", target = "userWalletId")
    WalletTransactionDTO toResponse(WalletTransactionResponse response);
}