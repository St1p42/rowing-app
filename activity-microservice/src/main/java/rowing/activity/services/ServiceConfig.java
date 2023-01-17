package rowing.activity.services;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;

@Data
public class ServiceConfig {

    @Value("${microserviceJWT}")
    private transient String token;

    @Value("${portNotification}")
    String portNotification;

    @Value("${urlNotification}")
    String urlNotification;

    @Value("${pathNotify}")
    String pathNotify;

    @Value("${portUsers}")
    String portUsers;

    @Value("${pathUserController}")
    String pathUserController;

    @Value("${pathUserAvailability}")
    String pathUserAvailability;
}
