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
import rowing.commons.models.NotificationRequestModel;
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

    @Value("${body.notification.accepted}")
    private String acceptedBody;

    @Value("${body.notification.rejected}")
    private String rejectedBody;

    @Value("${body.notification.deleted}")
    private String deletedBody;

    @Value("${body.notification.kicked}")
    private String kickedBody;

    @Value("${body.notification.withdrawn}")
    private String withdrawnBody;

    @Value("${body.notification.default}")
    private String defaultBody;

    @Value("${subject.notification.general}")
    private String subject;

    @Value("${subject.notification.activityChanges}")
    private String changesSubject;

    @Value("${body.notification.activityChanges}")
    private String changesBody;

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
            setVariables(notification);
            strategy.notifyUser(notification);
        } catch (RestClientException e) {
            if (e.getMessage().contains("404")) {
                System.out.println(e.getMessage());
                strategy =
                        strategyFactory.findStrategy(StrategyName.KAFKA);
                notification = new Notification(request, request.getUsername(), true);
                setVariables(notification);
                strategy.notifyUser(notification);
            } else {
                throw e;
            }
        } catch (Exception e) {
            throw e;
        }
    }

    /**
     * Sets the body variables of the notification object because
     * non-Spring-managed classes cannot load them by themselves.
     *
     * @param notification for which the variables need to be set
     */
    public void setVariables(Notification notification) {
        notification.setAcceptedBody(this.acceptedBody);

        notification.setRejectedBody(this.rejectedBody);

        notification.setDeletedBody(this.deletedBody);

        notification.setKickedBody(this.kickedBody);

        notification.setWithdrawnBody(this.withdrawnBody);

        notification.setDefaultBody(this.defaultBody);

        notification.setSubject(this.subject);

        notification.setChangesSubject(this.changesSubject);

        notification.setChangesBody(this.changesBody);
    }
}
