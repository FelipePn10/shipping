package redirex.shipping.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import redirex.shipping.dto.request.DepositRequestDto;
import redirex.shipping.entity.UserEntity;
import redirex.shipping.entity.UserWalletEntity;
import redirex.shipping.enums.CurrencyEnum;
import redirex.shipping.exception.InsufficientBalanceException;
import redirex.shipping.exception.StripePaymentException;
import redirex.shipping.repositories.UserRepository;
import redirex.shipping.repositories.UserWalletRepository;
import redirex.shipping.repositories.WalletTransactionRepository;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserWalletServiceImplTest {

    @Mock private UserRepository userRepository;
    @Mock private UserWalletRepository userWalletRepository;
    @Mock private WalletTransactionRepository walletTransactionRepository;
    @Mock private StripeService stripeService;
    @Mock private ExchangeRateService exchangeRateService;

    @InjectMocks
    private UserWalletServiceImpl walletService;

    private UserEntity user;
    private UserWalletEntity wallet;

    @BeforeEach
    void setUp() {
        user = new UserEntity();
        user.setId(1L);
        user.setEmail("test@example.com");

        wallet = new UserWalletEntity();
        wallet.setWalletId(1L);
        wallet.setUserId(user);
        wallet.setCurrency(CurrencyEnum.CNY);
        wallet.setBalance(BigDecimal.ZERO);
    }

    @Test
    void depositToWallet_Success() {
        // Configurar mocks
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userWalletRepository.findByUserIdAndCurrency(user, CurrencyEnum.CNY))
                .thenReturn(Optional.of(wallet));
        when(exchangeRateService.getExchangeRate(CurrencyEnum.BRL, CurrencyEnum.CNY))
                .thenReturn(new BigDecimal("0.80"));
        when(stripeService.processPayment("pm_test", 12500L, CurrencyEnum.BRL)).thenReturn(true);

        // Executar teste
        DepositRequestDto request = new DepositRequestDto(new BigDecimal("100"), "pm_test");
        var response = walletService.depositToWallet(1L, request);

        // Verificar resultados
        assertEquals("success", response.getStatus());
        assertEquals(new BigDecimal("95.00"), response.getAmount());
        assertEquals("5.00", response.getFee());
        assertEquals("125.00", response.getChargedAmount());

        // Verificar interações
        verify(userWalletRepository).save(wallet);
        verify(walletTransactionRepository).save(any());
    }

    @Test
    void depositToWallet_StripeFailure() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userWalletRepository.findByUserIdAndCurrency(user, CurrencyEnum.CNY))
                .thenReturn(Optional.of(wallet));
        when(exchangeRateService.getExchangeRate(CurrencyEnum.BRL, CurrencyEnum.CNY))
                .thenReturn(new BigDecimal("0.80"));
        when(stripeService.processPayment("pm_fail", 12500L, CurrencyEnum.BRL))
                .thenThrow(new StripePaymentException("Payment failed"));

        DepositRequestDto request = new DepositRequestDto(new BigDecimal("100"), "pm_fail");

        assertThrows(StripePaymentException.class, () -> {
            walletService.depositToWallet(1L, request);
        });

        // Verificar que o saldo NÃO foi alterado
        assertEquals(BigDecimal.ZERO, wallet.getBalance());
        verify(walletTransactionRepository, never()).save(any());
    }

    @Test
    void depositToWallet_CurrencyConversionEdgeCases() {
        // Testar taxa de câmbio zero
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userWalletRepository.findByUserIdAndCurrency(user, CurrencyEnum.CNY))
                .thenReturn(Optional.of(wallet));
        when(exchangeRateService.getExchangeRate(CurrencyEnum.BRL, CurrencyEnum.CNY))
                .thenReturn(BigDecimal.ZERO);

        DepositRequestDto request = new DepositRequestDto(new BigDecimal("100"), "pm_test");

        assertThrows(IllegalStateException.class, () -> {
            walletService.depositToWallet(1L, request);
        });
    }

    @Test
    void debitFromWallet_Success() throws InsufficientBalanceException {
        // Configurar carteira com saldo
        wallet.setBalance(new BigDecimal("500.00"));

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userWalletRepository.findByUserIdAndCurrency(user, CurrencyEnum.CNY))
                .thenReturn(Optional.of(wallet));

        // Executar débito
        walletService.debitFromWallet(1L, CurrencyEnum.CNY, new BigDecimal("200.00"),
                "ORDER_PAYMENT", "Compra #123", 456L, null);

        // Verificar resultados
        assertEquals(new BigDecimal("300.00"), wallet.getBalance());
        verify(walletTransactionRepository).save(any());
    }
}