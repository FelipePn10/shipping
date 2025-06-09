//package redirex.shipping.service;
//
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.ActiveProfiles;
//import org.springframework.transaction.annotation.Transactional;
//import redirex.shipping.G
//import redirex.shipping.dto.request.DepositRequestDto;
//import redirex.shipping.entity.UserEntity;
//import redirex.shipping.enums.CurrencyEnum;
//import redirex.shipping.exception.StripePaymentException;
//import redirex.shipping.repositories.UserRepository;
//import redirex.shipping.repositories.UserWalletRepository;
//
//import java.math.BigDecimal;
//import java.math.RoundingMode;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//@SpringBootTest(classes = ShippingApplication.class)
//@ActiveProfiles("test")
//@Transactional
//class FullIntegrationTest {
//
//    @Autowired
//    private UserRepository userRepository;
//
//    @Autowired
//    private UserWalletRepository userWalletRepository;
//
//    @Autowired
//    private UserWalletService userWalletService;
//
//    @Autowired
//    private MockStripeServiceTest mockStripeService; // Injeta o mock para controlar o comportamento
//
//    private UserEntity testUser;
//
//    @BeforeEach
//    void setUp() {
//        // Cria um usuário novo antes de cada teste para garantir um estado limpo
//        UserEntity user = new UserEntity();
//        user.setEmail("test.integration." + System.nanoTime() + "@example.com");
//        this.testUser = userRepository.saveAndFlush(user); // saveAndFlush garante que o usuário está no BD
//    }
//
//    @Test
//    @DisplayName("Deve executar o fluxo completo de depósito com sucesso")
//    void fullDepositIntegrationTest_shouldSucceed() {
//        // --- Arrange ---
//        BigDecimal targetDepositAmount = new BigDecimal("200.00");
//        String paymentMethodId = "pm_card_valid";
//
//        mockStripeService.setPaymentResult(paymentMethodId, true);
//
//        DepositRequestDto request = new DepositRequestDto();
//        request.setAmount(targetDepositAmount);
//        request.setCurrency(CurrencyEnum.CNY);
//        request.setPaymentMethodId(paymentMethodId);
//
//        // --- Act ---
//        var response = userWalletService.depositToWallet(testUser.getId(), request);
//
//        // --- Assert ---
//        assertNotNull(response);
//        assertEquals("success", response.getStatus());
//
//        BigDecimal fee = targetDepositAmount.multiply(new BigDecimal("0.05")).setScale(2, RoundingMode.HALF_UP);
//        BigDecimal expectedNetAmount = targetDepositAmount.subtract(fee);
//        assertEquals(0, expectedNetAmount.compareTo(response.getAmount()));
//
//        var wallet = userWalletRepository.findByUserIdAndCurrency(testUser, CurrencyEnum.CNY).orElseThrow();
//        assertEquals(0, expectedNetAmount.compareTo(wallet.getBalance()), "O saldo final da carteira está incorreto.");
//    }
//
//    @Test
//    @DisplayName("Deve falhar ao tentar depositar com um método de pagamento que gera erro no Stripe")
//    void fullDepositIntegrationTest_shouldFail_whenPaymentIsDeclined() {
//        // --- Arrange ---
//        String paymentMethodId = "pm_error";
//
//        DepositRequestDto request = new DepositRequestDto();
//        request.setAmount(new BigDecimal("100.00"));
//        request.setCurrency(CurrencyEnum.CNY);
//        request.setPaymentMethodId(paymentMethodId);
//
//        // --- Act & Assert ---
//        assertThrows(StripePaymentException.class, () -> {
//            userWalletService.depositToWallet(testUser.getId(), request);
//        }, "Deveria lançar StripePaymentException para pagamento recusado.");
//    }
//}