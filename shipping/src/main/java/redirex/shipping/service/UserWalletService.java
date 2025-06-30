package redirex.shipping.service;

import redirex.shipping.dto.request.DepositRequestDto;
import redirex.shipping.dto.response.WalletTransactionResponse;
import redirex.shipping.entity.UserWalletEntity;
import redirex.shipping.entity.WalletTransactionEntity;
import redirex.shipping.enums.CurrencyEnum;

import java.math.BigDecimal;
import java.util.List;

public interface UserWalletService {
    WalletTransactionResponse depositToWallet(Long userId, DepositRequestDto depositRequestDto);

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
        void debitFromWallet(Long userId, CurrencyEnum currency, BigDecimal amount,
                             String transactionType, String description, Long orderItemId,
                             Long shipmentId, BigDecimal chargedAmount, CurrencyEnum chargedCurrency);
    void refundToWallet(Long userId, CurrencyEnum currency, BigDecimal amount, String description, Long orderItemId);
    BigDecimal getUserWalletBalance(Long userId, CurrencyEnum currency);
    List<UserWalletEntity> listUserWallets(Long userId);
    List<WalletTransactionEntity> listWalletTransactions(Long userId, CurrencyEnum currency);
}