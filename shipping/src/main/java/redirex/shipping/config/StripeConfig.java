package redirex.shipping.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import redirex.shipping.service.StripeService;
import redirex.shipping.service.MockStripeService;
import redirex.shipping.service.StripeServiceImpl;

@Configuration
public class StripeConfig {

    @Bean
    @Profile("!test")  // Usa em todos os perfis exceto teste
    public StripeService realStripeService() {
        return new StripeServiceImpl();
    }

    @Bean
    @Profile("test")   // Usa apenas no perfil de teste
    public StripeService mockStripeService() {
        return new MockStripeService();
    }
}