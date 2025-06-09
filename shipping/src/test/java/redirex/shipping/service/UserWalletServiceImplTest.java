package redirex.shipping.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import redirex.shipping.dto.request.DepositRequestDto;
import redirex.shipping.dto.response.WalletTransactionResponse;
import redirex.shipping.entity.UserEntity;
import redirex.shipping.entity.UserWalletEntity;
import redirex.shipping.entity.WalletTransactionEntity;
import redirex.shipping.enums.CurrencyEnum;
import redirex.shipping.exception.StripePaymentException;
import redirex.shipping.repositories.UserRepository;
import redirex.shipping.repositories.UserWalletRepository;
import redirex.shipping.repositories.WalletTransactionRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes para o Serviço de Carteira (UserWalletServiceImpl)")
class UserWalletServiceImplTest {

    // --- Mocks ---
    @Mock private UserRepository userRepository;
    @Mock private UserWalletRepository userWalletRepository;
    @Mock private WalletTransactionRepository walletTransactionRepository;
    @Mock private StripeService stripeService;
    @Mock private ExchangeRateService exchangeRateService;

    @InjectMocks
    private UserWalletServiceImpl userWalletService;

    // --- Captors para verificar objetos salvos ---
    @Captor private ArgumentCaptor<UserWalletEntity> walletCaptor;
    @Captor private ArgumentCaptor<WalletTransactionEntity> transactionCaptor;

    // --- Constantes de Teste ---
    private static final Long USER_ID = 1L;
    private static final String VALID_PAYMENT_METHOD_ID = "pm_valid_card";
    private static final BigDecimal TARGET_CNY_AMOUNT = new BigDecimal("100.00"); // > 50
    private static final BigDecimal EXCHANGE_RATE_BRL_TO_CNY = new BigDecimal("1.85");

    // --- Objetos reutilizáveis ---
    private UserEntity user;
    private UserWalletEntity wallet;

    @BeforeEach
    void setUp() {
        user = new UserEntity();
        user.setId(USER_ID);

        wallet = new UserWalletEntity();
        wallet.setWalletId(1L);
        wallet.setUserId(user);
        wallet.setCurrency(CurrencyEnum.CNY);
        wallet.setBalance(BigDecimal.ZERO);
    }

    // Helper para criar um DTO válido
    private DepositRequestDto createValidDepositRequest(BigDecimal amount, String paymentMethodId) {
        DepositRequestDto request = new DepositRequestDto();
        request.setAmount(amount);
        request.setCurrency(CurrencyEnum.CNY);
        request.setPaymentMethodId(paymentMethodId);
        request.setSourceCurrency(CurrencyEnum.BRL.name());
        return request;
    }

    // --- Testes de Validação ---

    @Test
    @DisplayName("Deve lançar IllegalArgumentException para valor de depósito abaixo do limite")
    void depositToWallet_shouldThrowException_whenAmountIsBelowThreshold() {
        // Arrange
        DepositRequestDto request = createValidDepositRequest(new BigDecimal("50.00"), VALID_PAYMENT_METHOD_ID);

        // Act & Assert
        var exception = assertThrows(IllegalArgumentException.class, () -> userWalletService.depositToWallet(USER_ID, request));
        assertEquals("Target deposit amount (CNY) must be greater than zero.", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar IllegalArgumentException para PaymentMethodId nulo ou em branco")
    void depositToWallet_shouldThrowException_whenPaymentMethodIdIsBlank() {
        // Arrange
        DepositRequestDto request = createValidDepositRequest(TARGET_CNY_AMOUNT, " ");

        // Act & Assert
        var exception = assertThrows(IllegalArgumentException.class, () -> userWalletService.depositToWallet(USER_ID, request));
        assertEquals("Stripe PaymentMethod ID is required for deposit.", exception.getMessage());
    }

    // --- Teste de Sucesso ---

    @Test
    @DisplayName("Deve processar depósito com sucesso para um usuário existente")
    void depositToWallet_shouldSucceed_forExistingUser() {
        // Arrange
        DepositRequestDto request = createValidDepositRequest(TARGET_CNY_AMOUNT, VALID_PAYMENT_METHOD_ID);

        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
        when(userWalletRepository.findByUserIdAndCurrency(user, CurrencyEnum.CNY)).thenReturn(Optional.of(wallet));
        when(exchangeRateService.getExchangeRate(CurrencyEnum.BRL, CurrencyEnum.CNY)).thenReturn(EXCHANGE_RATE_BRL_TO_CNY);

        BigDecimal expectedBRLCharge = TARGET_CNY_AMOUNT.divide(EXCHANGE_RATE_BRL_TO_CNY, 2, RoundingMode.CEILING);
        long expectedBRLCents = expectedBRLCharge.multiply(new BigDecimal("100")).longValue();
        when(stripeService.processPayment(VALID_PAYMENT_METHOD_ID, expectedBRLCents, CurrencyEnum.BRL)).thenReturn(true);

        // Act
        WalletTransactionResponse response = userWalletService.depositToWallet(USER_ID, request);

        // Assert
        assertNotNull(response);
        assertEquals("success", response.getStatus());

        // Verifica o que foi salvo no banco
        verify(userWalletRepository).save(walletCaptor.capture());
        verify(walletTransactionRepository).save(transactionCaptor.capture());

        UserWalletEntity savedWallet = walletCaptor.getValue();
        WalletTransactionEntity savedTransaction = transactionCaptor.getValue();

        BigDecimal fee = TARGET_CNY_AMOUNT.multiply(new BigDecimal("0.05")).setScale(2, RoundingMode.HALF_UP);
        BigDecimal netAmount = TARGET_CNY_AMOUNT.subtract(fee);

        assertEquals(0, netAmount.compareTo(savedWallet.getBalance()), "Saldo da carteira deve ser atualizado com valor líquido.");
        assertEquals(0, netAmount.compareTo(savedTransaction.getAmount()), "Valor da transação deve ser o líquido.");
        assertEquals(0, fee.compareTo(savedTransaction.getTransactionFee()), "Taxa da transação deve ser correta.");
        assertEquals(0, TARGET_CNY_AMOUNT.compareTo(savedTransaction.getOriginalAmountDeposited()), "Valor original deve ser o alvo.");
        assertEquals(0, expectedBRLCharge.compareTo(new BigDecimal(savedTransaction.getChargedAmount())), "Valor cobrado em BRL deve ser correto.");
    }

    // --- Testes de Cenários de Falha e Comportamento ---

    @Test
    @DisplayName("Deve lançar exceção quando o usuário não for encontrado")
    void depositToWallet_shouldThrowException_whenUserNotFound() {
        // Arrange
        DepositRequestDto request = createValidDepositRequest(TARGET_CNY_AMOUNT, VALID_PAYMENT_METHOD_ID);
        when(userRepository.findById(USER_ID)).thenReturn(Optional.empty());

        // Act & Assert
        var exception = assertThrows(IllegalArgumentException.class, () -> userWalletService.depositToWallet(USER_ID, request));
        assertEquals("User not found for userId: " + USER_ID, exception.getMessage());
    }

    @Test
    @DisplayName("Deve criar uma nova carteira se o usuário não tiver uma")
    void depositToWallet_shouldCreateNewWallet_ifNotFound() {
        // Arrange
        DepositRequestDto request = createValidDepositRequest(TARGET_CNY_AMOUNT, VALID_PAYMENT_METHOD_ID);
        // Simula todos os passos de sucesso
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
        when(userWalletRepository.findByUserIdAndCurrency(user, CurrencyEnum.CNY)).thenReturn(Optional.empty()); // <-- A carteira não existe
        when(exchangeRateService.getExchangeRate(any(), any())).thenReturn(EXCHANGE_RATE_BRL_TO_CNY);
        when(stripeService.processPayment(any(), anyLong(), any())).thenReturn(true);

        // Act
        userWalletService.depositToWallet(USER_ID, request);

        // Assert
        // Verifica se save foi chamado duas vezes: uma para a nova carteira, outra para a atualização do saldo.
        verify(userWalletRepository, times(2)).save(any(UserWalletEntity.class));
    }

    @Test
    @DisplayName("Deve lançar IllegalStateException se a taxa de câmbio for inválida")
    void depositToWallet_shouldThrowException_whenExchangeRateIsInvalid() {
        // Arrange
        DepositRequestDto request = createValidDepositRequest(TARGET_CNY_AMOUNT, VALID_PAYMENT_METHOD_ID);
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
        when(userWalletRepository.findByUserIdAndCurrency(user, CurrencyEnum.CNY)).thenReturn(Optional.of(wallet));
        when(exchangeRateService.getExchangeRate(CurrencyEnum.BRL, CurrencyEnum.CNY)).thenReturn(BigDecimal.ZERO); // <-- Câmbio inválido

        // Act & Assert
        assertThrows(IllegalStateException.class, () -> userWalletService.depositToWallet(USER_ID, request));
    }

    @Test
    @DisplayName("Deve lançar StripePaymentException quando o pagamento falhar")
    void depositToWallet_shouldThrowStripePaymentException_whenPaymentFails() {
        // Arrange
        DepositRequestDto request = createValidDepositRequest(TARGET_CNY_AMOUNT, VALID_PAYMENT_METHOD_ID);
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
        when(userWalletRepository.findByUserIdAndCurrency(user, CurrencyEnum.CNY)).thenReturn(Optional.of(wallet));
        when(exchangeRateService.getExchangeRate(CurrencyEnum.BRL, CurrencyEnum.CNY)).thenReturn(EXCHANGE_RATE_BRL_TO_CNY);

        long expectedBRLCents = TARGET_CNY_AMOUNT.divide(EXCHANGE_RATE_BRL_TO_CNY, 2, RoundingMode.CEILING)
                .multiply(new BigDecimal("100")).longValue();

        // Simula a falha no Stripe
        when(stripeService.processPayment(VALID_PAYMENT_METHOD_ID, expectedBRLCents, CurrencyEnum.BRL))
                .thenThrow(new StripePaymentException("Seu cartão foi recusado."));

        // Act & Assert
        assertThrows(StripePaymentException.class, () -> userWalletService.depositToWallet(USER_ID, request));
    }
}