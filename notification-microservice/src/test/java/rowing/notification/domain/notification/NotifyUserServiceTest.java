package rowing.notification.domain.notification;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import rowing.commons.NotificationStatus;
import rowing.commons.requestModels.NotificationRequestModel;
import rowing.notification.authentication.AuthManager;
import rowing.notification.domain.notification.strategy.EmailStrategy;
import rowing.notification.domain.notification.strategy.KafkaStrategy;
import rowing.notification.domain.notification.strategy.StrategyFactory;
import rowing.notification.domain.notification.strategy.StrategyName;

import javax.naming.ConfigurationException;
import java.util.Date;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.http.HttpStatus.NOT_FOUND;

class NotifyUserServiceTest {
    private StrategyFactory strategyFactory;
    private RestTemplate restTemplate;

    private NotifyUserService notifyUserService;

    private NotificationRequestModel requestModel;

    @BeforeEach
    void setup() {
        // mock strategy factory
        strategyFactory = mock(StrategyFactory.class);
        EmailStrategy emailStrategy = mock(EmailStrategy.class);
        //when(emailStrategy).notifyUser(any(Notification.class))
        KafkaStrategy kafkaStrategy = mock(KafkaStrategy.class);
        when(strategyFactory.findStrategy(StrategyName.EMAIL)).thenReturn(emailStrategy);
        when(strategyFactory.findStrategy(StrategyName.KAFKA)).thenReturn(kafkaStrategy);

        // mock restTemplate
        restTemplate = mock(RestTemplate.class);

        // instantiate NotifyUserService
        notifyUserService = new NotifyUserService();
        notifyUserService.setUrl("localhost");
        notifyUserService.setPort("8080");
        notifyUserService.setEmailPath("/getEmail");
        notifyUserService.setToken("valitTokenBruh");
        notifyUserService.setRestTemplate(restTemplate);
        notifyUserService.setStrategyFactory(strategyFactory);

        // mock request model
        requestModel = mock(NotificationRequestModel.class);
        when(requestModel.getUsername()).thenReturn("test");
        when(requestModel.getActivityId()).thenReturn(new UUID(1, 1));
        when(requestModel.getStatus()).thenReturn(NotificationStatus.ACCEPTED);
    }

    @Test
    void exception1() {
        assertThrows(IllegalArgumentException.class, () -> {
            notifyUserService.notifyUser(null);
        });
    }

    @Test
    void exception2() {
        when(requestModel.getUsername()).thenReturn(null);
        assertThrows(IllegalArgumentException.class, () -> {
            notifyUserService.notifyUser(requestModel);
        });
    }

    @Test
    void exception3() {
        when(requestModel.getActivityId()).thenReturn(null);
        assertThrows(IllegalArgumentException.class, () -> {
            notifyUserService.notifyUser(requestModel);
        });
    }

    @Test
    void exception4() {
        when(requestModel.getStatus()).thenReturn(null);
        assertThrows(IllegalArgumentException.class, () -> {
            notifyUserService.notifyUser(requestModel);
        });
    }

    @Test
    void exception5() throws ConfigurationException {
        when(requestModel.getStatus()).thenReturn(NotificationStatus.CHANGES);
        when(requestModel.getDate()).thenReturn(null);
        when(requestModel.getLocation()).thenReturn(null);
        assertThrows(IllegalArgumentException.class, () -> {
            notifyUserService.notifyUser(requestModel);
        });

        when(requestModel.getLocation()).thenReturn("delft");

        ResponseEntity<String> response = new ResponseEntity<>("test@gmail.com", HttpStatus.OK);
        doReturn(response).when(restTemplate).exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class),
                (Class<?>) any(Class.class));

        notifyUserService.notifyUser(requestModel);

        when(requestModel.getLocation()).thenReturn(null);
        when(requestModel.getDate()).thenReturn(new Date());
        notifyUserService.notifyUser(requestModel);
    }

    @Test
    void sendKafkaMessage() throws ConfigurationException {
        doThrow(new RestClientException(NOT_FOUND.toString()))
                .when(restTemplate).exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class),
                (Class<?>) any(Class.class));

        notifyUserService.notifyUser(requestModel);
    }

    @Test
    void otherExceptionGetEmail() {
        doThrow(new RestClientException(HttpStatus.BAD_REQUEST.toString()))
                .when(restTemplate).exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class),
                (Class<?>) any(Class.class));

        assertThrows(RestClientException.class, () -> {
            notifyUserService.notifyUser(requestModel);
        });
    }

    @Test
    void nullUrlException() {
        notifyUserService.setUrl(null);

        assertThrows(ConfigurationException.class, () -> {
            notifyUserService.notifyUser(requestModel);
        }, "uri or token not configured properly");

        notifyUserService.setUrl("some");
        notifyUserService.setPort(null);

        assertThrows(ConfigurationException.class, () -> {
            notifyUserService.notifyUser(requestModel);
        }, "uri or token not configured properly");

        notifyUserService.setPort("some");
        notifyUserService.setEmailPath(null);

        assertThrows(ConfigurationException.class, () -> {
            notifyUserService.notifyUser(requestModel);
        }, "uri or token not configured properly");

        notifyUserService.setEmailPath("some");
        notifyUserService.setToken(null);

        assertThrows(ConfigurationException.class, () -> {
            notifyUserService.notifyUser(requestModel);
        }, "uri or token not configured properly");
    }
}