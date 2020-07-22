import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

import javax.annotation.Resource;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class ImApplication implements CommandLineRunner {

    @Resource
    private Server server;

    public static void main(String[] args) {
        SpringApplication.run(ImApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        Thread thread = new Thread() {
            public void run() {
                try {
                    server.startup();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        thread.start();

    }

}
