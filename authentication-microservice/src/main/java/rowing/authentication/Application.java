package rowing.authentication;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import rowing.commons.Certificates;

@SpringBootApplication()
public class Application {
    public static void main(String[] args) {
        Certificates.initialize();
        SpringApplication.run(Application.class, args);
    }
}


