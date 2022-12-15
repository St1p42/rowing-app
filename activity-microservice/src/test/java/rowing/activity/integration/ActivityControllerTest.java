package rowing.activity.integration;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.mock.http.server.reactive.MockServerHttpRequest.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import rowing.activity.authentication.AuthManager;
import rowing.activity.authentication.JwtTokenVerifier;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.ResultActions;
import rowing.activity.domain.entities.Activity;
import rowing.activity.domain.entities.Competition;
import rowing.activity.domain.entities.Training;
import rowing.activity.domain.repositories.ActivityRepository;
import rowing.commons.Gender;
import rowing.commons.Position;
import rowing.commons.entities.ActivityDTO;
//import com.fasterxml.jackson.*;

import javax.print.attribute.standard.Media;
import java.util.*;

@SpringBootTest
@ExtendWith(SpringExtension.class)
// activate profiles to have spring use mocks during auto-injection of certain beans.
@ActiveProfiles({"test", "mockTokenVerifier", "mockAuthenticationManager"})
//@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@AutoConfigureMockMvc
@EnableWebMvc
public class ActivityControllerTest {

    @Autowired
    private final MockMvc mockMvc;

    private final ObjectMapper objectMapper;

    @Autowired
    private transient JwtTokenVerifier mockJwtTokenVerifier;

    @Autowired
    private transient AuthManager mockAuthenticationManager;

    @MockBean
    private transient ActivityRepository mockActivityRepository;

    @Autowired
    public ActivityControllerTest(MockMvc mockMvc) {
        this.mockMvc = mockMvc;
        this.objectMapper = new ObjectMapper().registerModules(new Jdk8Module(), new JavaTimeModule());
    }

    @Test
    public void helloWorld() throws Exception {
        // Arrange
        // Notice how some custom parts of authorisation need to be mocked.
        // Otherwise, the integration test would never be able to authorise as the authorisation server is offline.
        when(mockAuthenticationManager.getNetId()).thenReturn("ExampleUser");
        when(mockJwtTokenVerifier.validateToken(anyString())).thenReturn(true);
        when(mockJwtTokenVerifier.getNetIdFromToken(anyString())).thenReturn("ExampleUser");

        // Act
        // Still include Bearer token as AuthFilter itself is not mocked
        ResultActions result = mockMvc.perform(get("/activity/hello")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer MockedToken"));

        // Assert
        result.andExpect(status().isOk());

        String response = result.andReturn().getResponse().getContentAsString();

        assertThat(response).isEqualTo("Hello ExampleUser");

    }

    @Test
    public void newActivity() throws Exception {

        // Arrange
        // Notice how some custom parts of authorisation need to be mocked.
        // Otherwise, the integration test would never be able to authorise as the authorisation server is offline.
        when(mockAuthenticationManager.getNetId()).thenReturn("ExampleUser");
        when(mockJwtTokenVerifier.validateToken(anyString())).thenReturn(true);
        when(mockJwtTokenVerifier.getNetIdFromToken(anyString())).thenReturn("ExampleUser");

        // Act
        // Still include Bearer token as AuthFilter itself is not mocked

        //Create a new activity
        Training mockActivity = new Training();
        mockActivity.setId(UUID.randomUUID());
        mockActivity.setOwner(UUID.randomUUID());
        mockActivity.setType("Training");
        mockActivity.setName("Test Activity");
        mockActivity.setStart(new Date());

        List<Position> positionList = new ArrayList<>();
        positionList.add(Position.COACH);
        positionList.add(Position.COX);
        mockActivity.setPositions(positionList);

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/activity/new")
                .header("Authorization", "Bearer MockedToken")
                .accept(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(mockActivity.getDto()))
                .contentType(MediaType.APPLICATION_JSON);

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        // Assert
        String response = result.getResponse().getContentAsString();

        assertThat(response).isEqualTo("Activity " + mockActivity.getId() + " was created successfully !");

    }

    @Test
    public void returnActivities() throws Exception {
        when(mockAuthenticationManager.getNetId()).thenReturn("ExampleUser");
        when(mockJwtTokenVerifier.validateToken(anyString())).thenReturn(true);
        when(mockJwtTokenVerifier.getNetIdFromToken(anyString())).thenReturn("ExampleUser");

        Training mockActivity = new Training();
        mockActivity.setId(UUID.randomUUID());
        mockActivity.setOwner(UUID.randomUUID());
        mockActivity.setName("Test Activity");
        mockActivity.setType("Training");
        mockActivity.setStart(new Date());

        List<Position> positionList = new ArrayList<>();
        positionList.add(Position.COACH);
        positionList.add(Position.COX);
        mockActivity.setPositions(positionList);
        MockMvcResultMatchers.content();

        Competition activity = new Competition();
        activity.setId(UUID.randomUUID());
        activity.setOwner(UUID.randomUUID());
        activity.setName("Test Activity2");
        activity.setType("Competition");
        activity.setStart(new Date());
        activity.setGender(Gender.MALE);

        List<Position> positionList2 = new ArrayList<>();
        positionList2.add(Position.PORT);
        positionList2.add(Position.COX);
        activity.setPositions(positionList2);
        MockMvcResultMatchers.content();

        List<Activity> activityList = new ArrayList<>();
        List<ActivityDTO> activitydtoList = new ArrayList<>();

        activityList.add(mockActivity);
        activityList.add(activity);
        activitydtoList.add(mockActivity.toDto());
        activitydtoList.add(activity.toDto());
        when(mockActivityRepository.findAll()).thenReturn(activityList);

        ResultActions result = mockMvc.perform(get("/activity/activityList")
                .header("Authorization", "Bearer MockedToken").contentType(MediaType.APPLICATION_JSON));

        // Assert
        result.andExpect(status().isOk());
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);

        String response = result.andReturn().getResponse().getContentAsString();

        //assertThat(response.replaceAll("\\{\"ActivityDTO\":", "").replaceAll("\"COX\"]}}", "\"COX\"]}")).
        // isEqualTo(mapper.writeValueAsString(activity_dto_list));
        JSONAssert.assertEquals(response.replaceAll("\\{\"ActivityDTO\":", "").replaceAll("]}}", "]}"),
                mapper.writeValueAsString(activitydtoList), false);
    }

    @Test
    public void deleteActivity() throws Exception {
        // Arrange
        // Notice how some custom parts of authorisation need to be mocked.
        // Otherwise, the integration test would never be able to authorise as the authorisation server is offline.
        UUID activityId = UUID.randomUUID();
        when(mockAuthenticationManager.getNetId()).thenReturn("ExampleUser");
        when(mockJwtTokenVerifier.validateToken(anyString())).thenReturn(true);
        when(mockJwtTokenVerifier.getNetIdFromToken(anyString())).thenReturn("ExampleUser");
        // Act
        // Still include Bearer token as AuthFilter itself is not mocked

        //Create a new activity
        Training mockActivity = new Training();
        mockActivity.setId(activityId);
        mockActivity.setOwner(UUID.randomUUID());
        mockActivity.setType("Training");
        mockActivity.setName("Test Activity");
        mockActivity.setStart(new Date());

        List<Position> positionList = new ArrayList<>();
        positionList.add(Position.COACH);
        positionList.add(Position.COX);
        mockActivity.setPositions(positionList);
        when(mockActivityRepository.findActivityById(activityId)).thenReturn(Optional.of(mockActivity));

        ResultActions result = mockMvc.perform(get("/activity/" + activityId + "/delete")
                .header("Authorization", "Bearer MockedToken").contentType(MediaType.APPLICATION_JSON));

        result.andExpect(status().isOk());
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);

        String response = result.andReturn().getResponse().getContentAsString();

        JSONAssert.assertEquals(response, mapper.writeValueAsString(mockActivity.toDto()), false);
    }

    @Test
    public void deleteActivityException() throws Exception {
        // Arrange
        // Notice how some custom parts of authorisation need to be mocked.
        // Otherwise, the integration test would never be able to authorise as the authorisation server is offline.
        UUID activityId = UUID.randomUUID();
        when(mockAuthenticationManager.getNetId()).thenReturn("ExampleUser");
        when(mockJwtTokenVerifier.validateToken(anyString())).thenReturn(true);
        when(mockJwtTokenVerifier.getNetIdFromToken(anyString())).thenReturn("ExampleUser");
        // Act
        // Still include Bearer token as AuthFilter itself is not mocked

        //Create a new activity
        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();
        Training mockActivity = new Training();
        mockActivity.setId(id1);
        mockActivity.setOwner(UUID.randomUUID());
        mockActivity.setType("Training");
        mockActivity.setName("Test Activity");
        mockActivity.setStart(new Date());

        List<Position> positionList = new ArrayList<>();
        positionList.add(Position.COACH);
        positionList.add(Position.COX);
        mockActivity.setPositions(positionList);
        when(mockActivityRepository.findActivityById(activityId)).thenReturn(Optional.of(mockActivity));

        ResultActions result = mockMvc.perform(get("/activity/" + id2 + "/delete")
                .header("Authorization", "Bearer MockedToken").contentType(MediaType.APPLICATION_JSON));

        result.andExpect(status().isNotFound());
    }
}
