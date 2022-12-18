package rowing.notification.domain.notification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import rowing.commons.requestModels.NotificationRequestModel;
import rowing.notification.authentication.AuthManager;
import rowing.notification.domain.notification.strategy.Strategy;
import rowing.notification.domain.notification.strategy.StrategyFactory;
import rowing.notification.domain.notification.strategy.StrategyName;


@Service
public class NotifyUserService {
    @Autowired
    private StrategyFactory strategyFactory;

    @Value("${uri.users.url}")
    private String url;

    @Value("${uri.users.port}")
    private String port;

    @Value("${uri.users.getemailpath}")
    private String emailPath;

    @Autowired
    private AuthManager authManager;

    @Autowired
    RestTemplate restTemplate;

    public void notifyUser(NotificationRequestModel request, String bearerToken){
        //building the request
        String uri = url + ":" + port + emailPath;
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(bearerToken);
        HttpEntity requestHttp = new HttpEntity(headers);

        //sending the request
        ResponseEntity<String> response = restTemplate.exchange(uri, HttpMethod.GET, requestHttp, String.class);

        //getting the strategy depending on the response
        Strategy strategy;
        Notification notification;
        if(!response.getStatusCode().equals(HttpStatus.NOT_FOUND)){
            strategy =
                    strategyFactory.findStrategy(StrategyName.EMAIL);
            notification = new Notification(request, response.getBody());
        } else {
            strategy =
                    strategyFactory.findStrategy(StrategyName.KAFKA);
            notification = new Notification(request, authManager.getUsername(), true);
        }

        // you can now call the methods defined in strategy.
        strategy.notifyUser(notification);
    }
}
