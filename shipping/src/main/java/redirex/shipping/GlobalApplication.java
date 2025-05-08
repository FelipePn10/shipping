package redirex.shipping;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import redirex.shipping.config.DotenvInitializer;

@SpringBootApplication
public class GlobalApplication {

	public static void main(String[] args) {
		SpringApplication app = new SpringApplication(GlobalApplication.class);
		app.addInitializers(new DotenvInitializer());

		try {
			app.run(args);
		} catch (Exception e) {
			System.err.println("🚨 Falha na inicialização da aplicação:");
			e.printStackTrace();
			System.exit(1);
		}
	}
}