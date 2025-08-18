package redirex.shipping;

import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import java.sql.Connection;

@Component
public class DatabaseTest implements CommandLineRunner {

    @Autowired
    private DataSource dataSource;

    @Override
    public void run(String... args) throws Exception {
        try (Connection conn = dataSource.getConnection()) {
            System.out.println("Conexão OK: " + conn.getMetaData().getURL());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
