package redirex.shipping;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories
public class GlobalApplication {
	public static void main(String[] args) {
		SpringApplication.run(GlobalApplication.class, args);
	}
}