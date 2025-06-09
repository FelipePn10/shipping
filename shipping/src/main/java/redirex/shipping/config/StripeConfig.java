package redirex.shipping.config;

import org.springframework.context.annotation.Configuration;

@Configuration
public class StripeConfig {
    // Nenhuma configuração adicional necessária, já que o bean StripeServiceImpl
    // é gerenciado pela anotação @Service com @Profile("!test") a partir de agora
}