package rowing.notification.integration;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.web.client.RestTemplate;
import rowing.commons.AvailabilityIntervals;
import rowing.commons.NotificationStatus;
import rowing.commons.entities.UpdateUserDTO;
import rowing.commons.entities.UserDTO;
import rowing.commons.entities.utils.JsonUtil;
import rowing.commons.models.NotificationRequestModel;
import rowing.notification.authentication.AuthManager;
import rowing.notification.authentication.JwtTokenVerifier;
import java.util.*;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest()
@ExtendWith(SpringExtension.class)
@ActiveProfiles({"test", "mockTokenVerifier", "mockAuthenticationManager"})
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class IntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private transient JwtTokenVerifier mockJwtTokenVerifier;

    @Autowired
    private transient AuthManager mockAuthenticationManager;

    @Autowired
    RestTemplate restTemplate;

    @Value("${uri.users.url}")
    String uri;

    @Value("${bearerToken}")
    String token;

    @Value("${testingTokenAlex}")
    String testingTokenAlex;

    /**
     * This method starts the user and authentication
     * microservice and sets up the database for the user microservice.
     *
     * @throws JsonProcessingException from parsing a json
     */
    @BeforeAll
    public void setup() throws JsonProcessingException {
        SpringApplicationBuilder uws = new SpringApplicationBuilder(rowing.user.Application.class);
        uws.run("--server.port=8084", "--spring.jpa.hibernate.ddl-auto=create-drop",
                "--jdbc.driverClassName=org.h2.Driver",
                "--jdbc.url=jdbc:h2:./user-microservice/example;DB_CLOSE_ON_EXIT=FALSE",
                "--hibernate.dialect=org.hibernate.dialect.H2Dialect",
                "--hibernate.hbm2ddl.auto=create");

        SpringApplicationBuilder auth = new SpringApplicationBuilder(rowing.authentication.Application.class);
        auth.run("--server.port=8081");

        //add email for user alex to database
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(testingTokenAlex);
        headers.setContentType(MediaType.APPLICATION_JSON);
        UpdateUserDTO updateUserDTO =
                new UpdateUserDTO(null, List.of(
                        new AvailabilityIntervals("monday", "13:00", "14:30")),
                        "aojica65@gmail.com", "alex",
                        "test", null, null, null, null);
        String body = JsonUtil.serialize(updateUserDTO);
        HttpEntity requestHttp = new HttpEntity(body, headers);
        //System.out.println(body);
        restTemplate.exchange("http://localhost:8084/user/update-user", HttpMethod.POST, requestHttp, UserDTO.class);
    }

    @Test
    public void testSendNotificationEmail() throws Exception {
        // Arrange
        // Notice how some custom parts of authorisation need to be mocked.
        // Otherwise, the integration test would never be able to authorise as the authorisation server is offline.
        when(mockAuthenticationManager.getUsername()).thenReturn("ExampleUser");
        when(mockJwtTokenVerifier.validateToken(anyString())).thenReturn(true);
        when(mockJwtTokenVerifier.getNetIdFromToken(anyString())).thenReturn("ExampleUser");

        // Act
        // Still include Bearer token as AuthFilter itself is not mocked
        NotificationRequestModel requestModel = new NotificationRequestModel("alex",
                NotificationStatus.ACCEPTED, new UUID(1, 1));

        // calls REST API internally
        ResultActions result = mockMvc.perform(post("/notify")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer MockedToken")
                .content(JsonUtil.serialize(requestModel)));

        // Assert
        result.andExpect(status().isOk());
    }

    @Test
    public void testSendNotificationChanges() throws Exception {
        // Arrange
        // Notice how some custom parts of authorisation need to be mocked.
        // Otherwise, the integration test would never be able to authorise as the authorisation server is offline.
        when(mockAuthenticationManager.getUsername()).thenReturn("ExampleUser");
        when(mockJwtTokenVerifier.validateToken(anyString())).thenReturn(true);
        when(mockJwtTokenVerifier.getNetIdFromToken(anyString())).thenReturn("ExampleUser");

        // Act
        // Still include Bearer token as AuthFilter itself is not mocked
        NotificationRequestModel requestModel = new NotificationRequestModel("alex",
                NotificationStatus.CHANGES, new UUID(1, 1), "Delft");

        // calls REST API internally
        ResultActions result = mockMvc.perform(post("/notify")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer MockedToken")
                .content(JsonUtil.serialize(requestModel)));

        // Assert
        result.andExpect(status().isOk());
    }

    @Test
    public void testSendNotificationFull() throws Exception {
        // Arrange
        // Notice how some custom parts of authorisation need to be mocked.
        // Otherwise, the integration test would never be able to authorise as the authorisation server is offline.
        when(mockAuthenticationManager.getUsername()).thenReturn("ExampleUser");
        when(mockJwtTokenVerifier.validateToken(anyString())).thenReturn(true);
        when(mockJwtTokenVerifier.getNetIdFromToken(anyString())).thenReturn("ExampleUser");

        // Act
        // Still include Bearer token as AuthFilter itself is not mocked
        NotificationRequestModel requestModel = new NotificationRequestModel("alex",
                NotificationStatus.ACTIVITY_FULL, new UUID(1, 1));

        // calls REST API internally
        ResultActions result = mockMvc.perform(post("/notify")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer MockedToken")
                .content(JsonUtil.serialize(requestModel)));

        // Assert
        result.andExpect(status().isOk());
    }

    @Test
    public void testSendNotificationKafka() throws Exception {
        // Arrange
        // Notice how some custom parts of authorisation need to be mocked.
        // Otherwise, the integration test would never be able to authorise as the authorisation server is offline.
        when(mockAuthenticationManager.getUsername()).thenReturn("ExampleUser");
        when(mockJwtTokenVerifier.validateToken(anyString())).thenReturn(true);
        when(mockJwtTokenVerifier.getNetIdFromToken(anyString())).thenReturn("ExampleUser");

        // Act
        // Still include Bearer token as AuthFilter itself is not mocked
        NotificationRequestModel requestModel = new NotificationRequestModel("notAlex",
                NotificationStatus.ACCEPTED, new UUID(1, 1));

        // calls REST API internally
        ResultActions result = mockMvc.perform(post("/notify")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer MockedToken")
                .content(JsonUtil.serialize(requestModel)));

        // Assert
        result.andExpect(status().isOk());
    }

    @Test
    public void testUnauthorizedNotification() throws Exception {
        // Arrange
        // Notice how some custom parts of authorisation need to be mocked.
        // Otherwise, the integration test would never be able to authorise as the authorisation server is offline.
        when(mockAuthenticationManager.getUsername()).thenReturn("ExampleUser");
        when(mockJwtTokenVerifier.validateToken(anyString())).thenReturn(true);
        when(mockJwtTokenVerifier.getNetIdFromToken(anyString())).thenReturn("ExampleUser");

        // Act
        // Still include Bearer token as AuthFilter itself is not mocked
        NotificationRequestModel requestModel = new NotificationRequestModel("notAlex",
                NotificationStatus.ACCEPTED, new UUID(1, 1));

        // calls REST API internally
        ResultActions result = mockMvc.perform(post("/notify")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.serialize(requestModel)));

        // Assert
        result.andExpect(status().isUnauthorized());
    }

    @Test
    public void testBadRequestUsers() throws Exception {
        // Arrange
        // Notice how some custom parts of authorisation need to be mocked.
        // Otherwise, the integration test would never be able to authorise as the authorisation server is offline.
        when(mockAuthenticationManager.getUsername()).thenReturn("ExampleUser");
        when(mockJwtTokenVerifier.validateToken(anyString())).thenReturn(true);
        when(mockJwtTokenVerifier.getNetIdFromToken(anyString())).thenReturn("ExampleUser");

        // Act
        // Still include Bearer token as AuthFilter itself is not mocked
        NotificationRequestModel requestModel = new NotificationRequestModel(null,
                NotificationStatus.ACCEPTED, new UUID(1, 1));

        // calls REST API internally
        ResultActions result = mockMvc.perform(post("/notify")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer MockedToken")
                .content(JsonUtil.serialize(requestModel)));

        // Assert
        result.andExpect(status().isBadRequest());
    }

    @Test
    public void testBadRequestNotification() throws Exception {
        // Arrange
        // Notice how some custom parts of authorisation need to be mocked.
        // Otherwise, the integration test would never be able to authorise as the authorisation server is offline.
        when(mockAuthenticationManager.getUsername()).thenReturn("ExampleUser");
        when(mockJwtTokenVerifier.validateToken(anyString())).thenReturn(true);
        when(mockJwtTokenVerifier.getNetIdFromToken(anyString())).thenReturn("ExampleUser");

        // Act
        // Still include Bearer token as AuthFilter itself is not mocked
        NotificationRequestModel requestModel = new NotificationRequestModel();

        // calls REST API internally
        ResultActions result = mockMvc.perform(post("/notify")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer MockedToken")
                .content(JsonUtil.serialize(requestModel)));

        // Assert
        result.andExpect(status().isBadRequest());

        requestModel = new NotificationRequestModel("test", NotificationStatus.CHANGES, new UUID(1, 1));
        // calls REST API internally
        result = mockMvc.perform(post("/notify")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer MockedToken")
                .content(JsonUtil.serialize(requestModel)));

        // Assert
        result.andExpect(status().isBadRequest());
    }

    @Test
    public void testSendNotificationLocationOK() throws Exception {
        // Arrange
        // Notice how some custom parts of authorisation need to be mocked.
        // Otherwise, the integration test would never be able to authorise as the authorisation server is offline.
        when(mockAuthenticationManager.getUsername()).thenReturn("ExampleUser");
        when(mockJwtTokenVerifier.validateToken(anyString())).thenReturn(true);
        when(mockJwtTokenVerifier.getNetIdFromToken(anyString())).thenReturn("ExampleUser");

        // Act
        // Still include Bearer token as AuthFilter itself is not mocked
        NotificationRequestModel requestModel = new NotificationRequestModel("alex",
                NotificationStatus.CHANGES, new UUID(1, 1), "delft");

        // calls REST API internally
        ResultActions result = mockMvc.perform(post("/notify")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer MockedToken")
                .content(JsonUtil.serialize(requestModel)));

        // Assert
        result.andExpect(status().isOk());
    }

    @Test
    public void testSendNotificationDateOK() throws Exception {
        // Arrange
        // Notice how some custom parts of authorisation need to be mocked.
        // Otherwise, the integration test would never be able to authorise as the authorisation server is offline.
        when(mockAuthenticationManager.getUsername()).thenReturn("ExampleUser");
        when(mockJwtTokenVerifier.validateToken(anyString())).thenReturn(true);
        when(mockJwtTokenVerifier.getNetIdFromToken(anyString())).thenReturn("ExampleUser");

        // Act
        // Still include Bearer token as AuthFilter itself is not mocked
        NotificationRequestModel requestModel = new NotificationRequestModel("alex",
                NotificationStatus.CHANGES, new UUID(1, 1), new Date());

        // calls REST API internally
        ResultActions result = mockMvc.perform(post("/notify")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer MockedToken")
                .content(JsonUtil.serialize(requestModel)));

        // Assert
        result.andExpect(status().isOk());
    }

    @Test
    public void testSendNotificationDateKafka() throws Exception {
        // Arrange
        // Notice how some custom parts of authorisation need to be mocked.
        // Otherwise, the integration test would never be able to authorise as the authorisation server is offline.
        when(mockAuthenticationManager.getUsername()).thenReturn("ExampleUser");
        when(mockJwtTokenVerifier.validateToken(anyString())).thenReturn(true);
        when(mockJwtTokenVerifier.getNetIdFromToken(anyString())).thenReturn("ExampleUser");

        // Act
        // Still include Bearer token as AuthFilter itself is not mocked
        NotificationRequestModel requestModel = new NotificationRequestModel("notAlex",
                NotificationStatus.CHANGES, new UUID(1, 1), new Date());

        // calls REST API internally
        ResultActions result = mockMvc.perform(post("/notify")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer MockedToken")
                .content(JsonUtil.serialize(requestModel)));

        // Assert
        result.andExpect(status().isOk());
    }

    @Test
    public void testSendNotificationLocationKafka() throws Exception {
        // Arrange
        // Notice how some custom parts of authorisation need to be mocked.
        // Otherwise, the integration test would never be able to authorise as the authorisation server is offline.
        when(mockAuthenticationManager.getUsername()).thenReturn("ExampleUser");
        when(mockJwtTokenVerifier.validateToken(anyString())).thenReturn(true);
        when(mockJwtTokenVerifier.getNetIdFromToken(anyString())).thenReturn("ExampleUser");

        // Act
        // Still include Bearer token as AuthFilter itself is not mocked
        NotificationRequestModel requestModel = new NotificationRequestModel("notAlex",
                NotificationStatus.CHANGES, new UUID(1, 1), "delft");

        // calls REST API internally
        ResultActions result = mockMvc.perform(post("/notify")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer MockedToken")
                .content(JsonUtil.serialize(requestModel)));

        // Assert
        result.andExpect(status().isOk());
    }

}
