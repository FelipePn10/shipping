package redirex.shipping.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import redirex.shipping.dto.request.DepositRequestDto;
import redirex.shipping.entity.UserEntity;
import redirex.shipping.enums.CurrencyEnum;
import redirex.shipping.repositories.UserRepository;
import redirex.shipping.repositories.UserWalletRepository;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@ActiveProfiles("test")
class FullIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserWalletRepository userWalletRepository;

    @Autowired
    private UserWalletServiceImpl walletService;

    @Autowired
    private MockStripeServiceTest mockStripeService;

    @Test
    void fullDepositIntegrationTest() {
        // Configurar mock do Stripe
        mockStripeService.setPaymentResult("pm_test", true);

        // Criar usuário de teste
        UserEntity user = new UserEntity();
        user.setEmail("test@integration.com");
        user = userRepository.save(user);

        // Executar depósito
        DepositRequestDto request = new DepositRequestDto(new BigDecimal("200.00"), "pm_test");
        var response = walletService.depositToWallet(user.getId(), request);

        // Verificar resultados
        assertEquals("success", response.getStatus());
        assertEquals(new BigDecimal("190.00"), response.getAmount());

        // Verificar banco de dados
        var wallet = userWalletRepository.findByUserIdAndCurrency(user, CurrencyEnum.CNY)
                .orElseThrow();

        assertEquals(new BigDecimal("190.00"), wallet.getBalance());
    }
}