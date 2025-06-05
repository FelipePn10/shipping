package redirex.shipping.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import redirex.shipping.service.StripeService;
import redirex.shipping.service.RealStripeService;
import redirex.shipping.service.MockStripeService;

@Configuration
public class StripeConfig {

    @Bean
    @Profile("!test")  // Usa em todos os perfis exceto teste
    public StripeService realStripeService() {
        return new RealStripeService();
    }

    @Bean
    @Profile("test")   // Usa apenas no perfil de teste
    public StripeService mockStripeService() {
        return new MockStripeService();
    }
}