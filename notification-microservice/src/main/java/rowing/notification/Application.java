package rowing.notification;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import rowing.notification.domain.notification.EmailService;

/**
 * Example microservice application.
 */
@SpringBootApplication
public class Application {
    @Autowired
    private transient EmailService senderService;

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}
