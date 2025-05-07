package redirex.shipping.config;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

import java.util.HashMap;
import java.util.Map;

public class DotenvInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        try {
            Dotenv dotenv = Dotenv.configure()
                    .directory("C:/Users/Panosso/projetos/backend/java/picpay/shipping")
                    .ignoreIfMalformed()
                    .ignoreIfMissing()
                    .load();
            System.out.println("Dotenv loaded successfully from: C:/Users/Panosso/projetos/backend/java/picpay/shipping/.env");

            Map<String, Object> envVariables = new HashMap<>();
            dotenv.entries().forEach(entry -> {
                envVariables.put(entry.getKey(), entry.getValue());
                System.out.println("Loaded env: " + entry.getKey() + "=" + entry.getValue());
            });

            ConfigurableEnvironment environment = applicationContext.getEnvironment();
            environment.getPropertySources().addFirst(new MapPropertySource("dotenvProperties", envVariables));
        } catch (Exception e) {
            System.err.println("Failed to load .env file: " + e.getMessage());
            e.printStackTrace();
        }
    }
}