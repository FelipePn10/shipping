package redirex.shipping.service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import redirex.shipping.service.StripeService;
import redirex.shipping.service.MockStripeServiceTest;

@Configuration
@Profile("test")
public class TestStripeConfig {
    @Bean
    public StripeService mockStripeService() {
        return new MockStripeServiceTest();
    }
}