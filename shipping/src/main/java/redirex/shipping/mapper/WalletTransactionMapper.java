package redirex.shipping.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import redirex.shipping.dto.request.WalletTransactionRequest;
import redirex.shipping.dto.response.WalletTransactionResponse;
import redirex.shipping.entity.WalletTransactionEntity;

@Mapper(componentModel = "spring")
public interface WalletTransactionMapper {
    @Mapping(source = "userWallet.walletId", target = "userWalletId")
    WalletTransactionRequest toDTO(WalletTransactionEntity entity);

    @Mapping(target = "userWallet", ignore = true)
    WalletTransactionEntity toEntity(WalletTransactionRequest dto);

    @Mapping(source = "userWalletId", target = "userWalletId")
    WalletTransactionRequest toResponse(WalletTransactionResponse response);
}