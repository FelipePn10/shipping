package redirex.shipping.service;

import redirex.shipping.dto.request.DepositRequestDto;
import redirex.shipping.entity.UserWalletEntity;
import redirex.shipping.entity.WalletTransactionEntity;
import redirex.shipping.enums.CurrencyEnum;
import redirex.shipping.exception.InsufficientBalanceException;

import java.math.BigDecimal;
import java.util.List;

public interface UserWalletService {
    void depositToWallet(Long userId, DepositRequestDto depositRequestDto);
    void debitFromWallet(Long userId, CurrencyEnum currency, BigDecimal amount, String transactionType,
                         String description, Long orderItemId, Long shipmentId) throws InsufficientBalanceException;
    void refundToWallet(Long userId, CurrencyEnum currency, BigDecimal amount, String description, Long orderItemId);
    BigDecimal getUserWalletBalance(Long userId, CurrencyEnum currency);
    List<UserWalletEntity> listUserWallets(Long userId);
    List<WalletTransactionEntity> listWalletTransactions(Long userId, CurrencyEnum currency);
}