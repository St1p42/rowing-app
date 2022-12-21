package rowing.notification.domain.notification;

import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import rowing.commons.NotificationStatus;
import rowing.commons.requestModels.NotificationRequestModel;
import rowing.notification.authentication.AuthManager;
import rowing.notification.domain.notification.strategy.Strategy;
import rowing.notification.domain.notification.strategy.StrategyFactory;
import rowing.notification.domain.notification.strategy.StrategyName;

import javax.naming.ConfigurationException;


@Data
@Service
public class NotifyUserService {
    @Autowired
    private transient StrategyFactory strategyFactory;

    @Value("${uri.users.url}")
    private transient String url;

    @Value("${uri.users.port}")
    private transient String port;

    @Value("${uri.users.getemailpath}")
    private transient String emailPath;

    @Value("${microserviceJWT}")
    String token;

    @Autowired
    transient RestTemplate restTemplate;

    /**
     * This method calls the endpoint inside the users microservice to receive the user's email address.
     *  If the email address is found then it continues with the EMAIL strategy, else with the KAFKA strategy.

     * @param request - request that was received
     */
    public void notifyUser(NotificationRequestModel request) throws ConfigurationException {
        // data validation
        if (request == null || request.getUsername() == null
                || request.getActivityId() == null || request.getStatus() == null) {
            throw new IllegalArgumentException();
        }

        if (request.getStatus().equals(NotificationStatus.CHANGES)
                && (request.getDate() == null && request.getLocation() == null)) {
            throw new IllegalArgumentException();
        }

        if (url == null || port == null || emailPath == null || token == null) {
            throw new ConfigurationException("uri or token not configured properly");
        }

        //building the request
        String uri = url + ":" + port + emailPath;
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        String body = "{\"username\":\"" + request.getUsername() + "\"}";
        HttpEntity requestHttp = new HttpEntity(body, headers);

        //getting the strategy depending on the response
        Strategy strategy;
        Notification notification;

        //sending the request
        try {
            ResponseEntity<String> response = restTemplate.exchange(uri, HttpMethod.GET, requestHttp, String.class);
            System.out.println(response);
            strategy =
                    strategyFactory.findStrategy(StrategyName.EMAIL);
            notification = new Notification(request, response.getBody());
            strategy.notifyUser(notification);
        } catch (RestClientException e) {
            if (e.getMessage().contains("404")) {
                System.out.println(e.getMessage());
                strategy =
                        strategyFactory.findStrategy(StrategyName.KAFKA);
                notification = new Notification(request, request.getUsername(), true);
                strategy.notifyUser(notification);
            } else {
                throw e;
            }
        } catch (Exception e) {
            throw e;
        }
    }
}
