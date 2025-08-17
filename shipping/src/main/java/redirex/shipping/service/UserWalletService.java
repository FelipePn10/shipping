package redirex.shipping.service;

import redirex.shipping.dto.request.DepositRequestDto;
import redirex.shipping.dto.response.WalletTransactionResponse;
import redirex.shipping.entity.UserWalletEntity;
import redirex.shipping.entity.WalletTransactionEntity;
import redirex.shipping.enums.CurrencyEnum;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface UserWalletService {
    WalletTransactionResponse depositToWallet(UUID userId, DepositRequestDto depositRequestDto);

        /**
         *
         * @param userId            ID do usuário.
         * @param currency          Moeda da carteira a ser debitada.
         * @param amount            Valor a ser debitado.
         * @param transactionType   Tipo da transação (ex: "ORDER_PAYMENT").
         * @param description       Descrição da transação.
         * @param orderItemId       ID do pedido relacionado (se houver).
         * @param shipmentId        ID do envio relacionado (se houver).
         * @param chargedAmount     O valor original da cobrança.
         * @param chargedCurrency   A moeda original da cobrança.
         */
        void debitFromWallet(UUID userId, CurrencyEnum currency, BigDecimal amount,
                             String transactionType, String description, UUID orderItemId,
                             UUID shipmentId, BigDecimal chargedAmount, CurrencyEnum chargedCurrency);
    void refundToWallet(UUID userId, CurrencyEnum currency, BigDecimal amount, String description, UUID orderItemId);
    BigDecimal getUserWalletBalance(UUID userId, CurrencyEnum currency);
    List<UserWalletEntity> listUserWallets(UUID userId);
    List<WalletTransactionEntity> listWalletTransactions(UUID userId, CurrencyEnum currency);
}