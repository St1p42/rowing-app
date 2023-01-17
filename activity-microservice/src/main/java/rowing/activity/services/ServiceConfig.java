package rowing.activity.services;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;

@Getter
enum ServiceConfig {
    value;

    @Value("${microserviceJWT}")
    static String token;

    @Value("${portNotification}")
    static
    String portNotification;

    @Value("${urlNotification}")
    static String urlNotification;

    @Value("${pathNotify}")
    static String pathNotify;

    @Value("${portUsers}")
    static String portUsers;

    @Value("${pathUserController}")
    static String pathUserController;

    @Value("${pathUserAvailability}")
    static String pathUserAvailability;
}
