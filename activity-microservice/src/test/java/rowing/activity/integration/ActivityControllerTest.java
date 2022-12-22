package rowing.activity.integration;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import rowing.activity.authentication.AuthManager;
import rowing.activity.authentication.JwtTokenVerifier;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.ResultActions;
import rowing.activity.domain.CompetitionBuilder;
import rowing.activity.domain.Director;
import rowing.activity.domain.TrainingBuilder;
import rowing.activity.domain.entities.Activity;
import rowing.activity.domain.entities.Competition;
import rowing.activity.domain.entities.Match;
import rowing.activity.domain.repositories.ActivityRepository;
import rowing.activity.domain.repositories.MatchRepository;
import rowing.activity.domain.utils.Builder;
import rowing.commons.AvailabilityIntervals;
import rowing.commons.Gender;
import rowing.commons.NotificationStatus;
import rowing.commons.Position;
import rowing.commons.entities.ActivityDTO;
import rowing.commons.entities.MatchingDTO;
import rowing.commons.entities.UserDTO;
import rowing.commons.entities.utils.JsonUtil;
import rowing.commons.models.NotificationRequestModel;
import rowing.commons.models.UserDTORequestModel;
//import com.fasterxml.jackson.*;

import javax.persistence.Transient;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@SpringBootTest
@ExtendWith(SpringExtension.class)
// activate profiles to have spring use mocks during auto-injection of certain beans.
@ActiveProfiles({"test", "mockTokenVerifier", "mockAuthenticationManager"})
//@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@AutoConfigureMockMvc
@EnableWebMvc
@TestPropertySource(properties = { "spring.config.name=application-test"})
public class ActivityControllerTest {

    @Autowired
    private final MockMvc mockMvc;

    private final ObjectMapper objectMapper;

    @Autowired
    private transient JwtTokenVerifier mockJwtTokenVerifier;

    private MockRestServiceServer mockServer;

    @Autowired
    private transient AuthManager mockAuthenticationManager;

    @Autowired
    private transient ActivityRepository mockActivityRepository;

    @Autowired
    private transient MatchRepository mockMatchRepository;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    public ActivityControllerTest(MockMvc mockMvc) {
        this.mockMvc = mockMvc;
        this.objectMapper = new ObjectMapper().registerModules(new Jdk8Module(), new JavaTimeModule());
    }

    Activity amateurTraining;
    Activity amateurCompetition;
    Date amateurTrainingDate;
    Date amateurCompetitionDate;
    MatchingDTO match;
    UUID trainingId;
    UUID competitionId;
    List<AvailabilityIntervals> availability;
    UserDTO exampleUser;
    List<String> coxCertificates;


    /**
     * Function that inits the basic activity.
     *
     * @throws ParseException exception for wrong format
     */
    @BeforeEach
    public void init() throws ParseException {
        // Arrange
        // Notice how some custom parts of authorisation need to be mocked.
        // Otherwise, the integration test would never be able to authorise as the authorisation server is offline.
        when(mockAuthenticationManager.getUsername()).thenReturn("Admin");
        when(mockJwtTokenVerifier.validateToken(anyString())).thenReturn(true);
        when(mockJwtTokenVerifier.getNetIdFromToken(anyString())).thenReturn("Admin");

        mockServer = MockRestServiceServer.createServer(restTemplate);
        // Act
        // Still include Bearer token as AuthFilter itself is not mocked

        String dateString = "26-09-3043 14:05:05";
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
        amateurTrainingDate = formatter.parse(dateString);
        trainingId = UUID.randomUUID();

        dateString = "27-09-3043 16:05:05"; // Creating new competition details
        formatter = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
        amateurCompetitionDate = formatter.parse(dateString);
        competitionId = UUID.randomUUID();

        Builder trainingBuilder = new TrainingBuilder();
        Director director = new Director();

        List<Position> positionList = new ArrayList<>();
        positionList.add(Position.COACH);
        List<String> applicantList = new ArrayList<>();

        director.constructTraining((TrainingBuilder) trainingBuilder, UUID.randomUUID(),
                "Amateur Training", "Admin", "Training",
                amateurTrainingDate, positionList, applicantList);
        amateurTraining = trainingBuilder.build();

        Builder competitionBuilder = new CompetitionBuilder();
        director.constructCompetition((CompetitionBuilder) competitionBuilder, UUID.randomUUID(),
                "Amateur Competition", "Admin", "Competition",
                amateurCompetitionDate,  Gender.MALE, "TUDelft", positionList, applicantList);
        amateurCompetition = competitionBuilder.build();

        availability = new ArrayList<AvailabilityIntervals>();
        availability.add(new AvailabilityIntervals("wednesday", "14:05", "14:06"));
        availability.add(new AvailabilityIntervals("thursday", "16:05", "16:06"));
        match = new MatchingDTO(UUID.randomUUID(), null,
                "Admin", Position.COX, Gender.MALE, true, "TUDelft",
                availability, null);

        exampleUser = new UserDTO("Efe", new ArrayList<>(Arrays.asList(Position.PORT, Position.COACH)),
                availability, "extra.efeunluyurt@gmail.com", "Efe", "Unluyurt",
                coxCertificates, Gender.MALE, "TU DELFT", true);
    }

    @AfterEach
    public void end() {
        mockActivityRepository.deleteAll();
        mockMatchRepository.deleteAll();
    }

    @Test
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void newActivity() throws Exception {

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/activity/new")
                .header("Authorization", "Bearer MockedToken")
                .accept(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(amateurTraining.getDto()))
                .contentType(MediaType.APPLICATION_JSON);

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        // Assert
        String response = result.getResponse().getContentAsString();

        assertThat(response).isEqualTo("Activity " + amateurTraining.getId() + " was created successfully !");

    }

    @Test
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void returnActivities() throws Exception {
        Competition activity = new Competition();
        activity.setId(UUID.randomUUID());
        activity.setOwner("Admin");
        activity.setName("Test Activity2");
        activity.setType("Competition");
        activity.setStart(amateurTrainingDate);
        activity.setGender(Gender.MALE);

        List<Position> positionList2 = new ArrayList<>();
        positionList2.add(Position.PORT);
        positionList2.add(Position.COX);
        activity.setPositions(positionList2);

        List<String> applicantList1 = new ArrayList<>();
        activity.setApplicants(applicantList1);

        List<ActivityDTO> activityDTOList = new ArrayList<>();

        amateurTraining = mockActivityRepository.save(amateurTraining);
        activity = mockActivityRepository.save(activity);

        activityDTOList.add(amateurTraining.toDto());
        activityDTOList.add(activity.toDto());

        ResultActions result = mockMvc.perform(get("/activity/activityList")
                .header("Authorization", "Bearer MockedToken").contentType(MediaType.APPLICATION_JSON));

        // Assert
        result.andExpect(status().isOk());
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);

        String response = result.andReturn().getResponse().getContentAsString();
        System.out.println(response.replaceAll("\\{\"ActivityDTO\":", "").replaceAll("]}}", "]}"));
        System.out.println(mapper.writeValueAsString(activityDTOList));

        //assertThat(response.replaceAll("\\{\"ActivityDTO\":", "").replaceAll("\"COX\"]}}", "\"COX\"]}")).
        // isEqualTo(mapper.writeValueAsString(activity_dto_list));
        JSONAssert.assertEquals(response.replaceAll("\\{\"ActivityDTO\":", "").replaceAll("]}}", "]}"),
                mapper.writeValueAsString(activityDTOList), false);
    }

    @Test
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void activityExpired() throws Exception {

        String dateString2 = "26-09-1884";
        SimpleDateFormat formatter2 = new SimpleDateFormat("dd-MM-yyyy");
        Date date = formatter2.parse(dateString2);

        Competition activity = new Competition();
        activity.setId(UUID.randomUUID());
        activity.setOwner("Admin");
        activity.setName("Test Activity2");
        activity.setType("Competition");
        activity.setStart(date);
        activity.setGender(Gender.MALE);

        List<Position> positionList2 = new ArrayList<>();
        positionList2.add(Position.PORT);
        positionList2.add(Position.COX);
        activity.setPositions(positionList2);

        List<String> applicantList1 = new ArrayList<>();
        activity.setApplicants(applicantList1);

        List<ActivityDTO> activityDTOList = new ArrayList<>();

        amateurCompetition = mockActivityRepository.save(amateurTraining);
        activity = mockActivityRepository.save(activity);

        activityDTOList.add(amateurCompetition.toDto());

        ResultActions result = mockMvc.perform(get("/activity/activityList")
                .header("Authorization", "Bearer MockedToken").contentType(MediaType.APPLICATION_JSON));

        // Assert
        result.andExpect(status().isOk());
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);

        String response = result.andReturn().getResponse().getContentAsString();

        // assertThat(response.replaceAll("\\{\"ActivityDTO\":", "").replaceAll("\"COX\"]}}", "\"COX\"]}")).
        // isEqualTo(mapper.writeValueAsString(activity_dto_list));
        JSONAssert.assertEquals(response.replaceAll("\\{\"ActivityDTO\":", "").replaceAll("]}}", "]}"),
                mapper.writeValueAsString(activityDTOList), false);
    }

    @Test
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void deleteActivity() throws Exception {
        //Create a new activity
        List<Position> positionList = new ArrayList<>();
        positionList.add(Position.COACH);
        positionList.add(Position.COX);
        amateurTraining.setPositions(positionList);

        amateurTraining = mockActivityRepository.save(amateurTraining);
        trainingId = amateurTraining.getId();

        ResultActions result = mockMvc.perform(get("/activity/" + trainingId + "/delete")
                .header("Authorization", "Bearer MockedToken").contentType(MediaType.APPLICATION_JSON));

        result.andExpect(status().isOk());
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);

        String response = result.andReturn().getResponse().getContentAsString();

        JSONAssert.assertEquals(response, mapper.writeValueAsString(amateurTraining.toDto()), false);
    }

    @Test
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void deleteActivityException() throws Exception {
        amateurTraining = mockActivityRepository.save(amateurTraining);
        trainingId = amateurTraining.getId();

        UUID id2 = UUID.randomUUID();
        ResultActions result = mockMvc.perform(get("/activity/" + id2 + "/delete")
                .header("Authorization", "Bearer MockedToken").contentType(MediaType.APPLICATION_JSON));

        result.andExpect(status().isNotFound());
    }

    @Test
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void testSignUpTraining() throws Exception {
        amateurTraining = mockActivityRepository.save(amateurTraining);
        trainingId = amateurTraining.getId();

        match.setActivityId(trainingId); // Make sure to set for the activity you want to sign up for

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/activity/sign/{activityId}", trainingId)
                .header("Authorization", "Bearer MockedToken")
                .accept(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(match))
                .contentType(MediaType.APPLICATION_JSON);

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        // Assert
        String response = result.getResponse().getContentAsString();
        assertThat(response).isEqualTo("User " + match.getUserId()
                + " signed up for activity : " + match.getActivityId().toString());
    }

    @Test
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void testSignUpCompetition() throws Exception {
        amateurCompetition = mockActivityRepository.save(amateurCompetition);
        competitionId = amateurCompetition.getId();

        match.setActivityId(competitionId);

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/activity/sign/{activityId}", competitionId)
                .header("Authorization", "Bearer MockedToken")
                .accept(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(match))
                .contentType(MediaType.APPLICATION_JSON);

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        // Assert
        String response = result.getResponse().getContentAsString();
        assertThat(response).isEqualTo("User " + match.getUserId()
                + " signed up for activity : " + match.getActivityId().toString());
    }

    @Test
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void testSignUpCompetitionGenderException() throws Exception {
        amateurCompetition = mockActivityRepository.save(amateurCompetition);
        competitionId = amateurCompetition.getId();

        match.setActivityId(competitionId);
        match.setGender(Gender.FEMALE);

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/activity/sign/{activityId}", competitionId)
                .header("Authorization", "Bearer MockedToken")
                .accept(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(match))
                .contentType(MediaType.APPLICATION_JSON);

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        // Assert
        String response = result.getResponse().getContentAsString();
        assertThat(response).isEqualTo("User does not fit gender requirements !");
    }

    @Test
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void testSignUpCompetitionOrganisationException() throws Exception {
        amateurCompetition = mockActivityRepository.save(amateurCompetition);
        competitionId = amateurCompetition.getId();

        match.setActivityId(competitionId);
        match.setOrganisation("TUEindhoven");

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/activity/sign/{activityId}", competitionId)
                .header("Authorization", "Bearer MockedToken")
                .accept(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(match))
                .contentType(MediaType.APPLICATION_JSON);

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        // Assert
        String response = result.getResponse().getContentAsString();
        assertThat(response).isEqualTo("User is not part of the organisation !");
    }

    @Test
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void testSignUpCompetitionCompetitiveException() throws Exception {
        amateurCompetition = mockActivityRepository.save(amateurCompetition);
        competitionId = amateurCompetition.getId();

        match.setActivityId(competitionId);
        match.setCompetitive(false);

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/activity/sign/{activityId}", competitionId)
                .header("Authorization", "Bearer MockedToken")
                .accept(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(match))
                .contentType(MediaType.APPLICATION_JSON);

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        // Assert
        String response = result.getResponse().getContentAsString();
        assertThat(response).isEqualTo("User is not competitive!");
    }

    @Test
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void testSignUpAvailabilityException() throws Exception {
        List<AvailabilityIntervals> newAvailability = new ArrayList<AvailabilityIntervals>();
        newAvailability.add(new AvailabilityIntervals("monday", "12:00", "12:05"));
        match.setAvailability(newAvailability);

        amateurCompetition = mockActivityRepository.save(amateurCompetition);
        competitionId = amateurCompetition.getId();
        match.setActivityId(competitionId);

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/activity/sign/{activityId}", competitionId)
                .header("Authorization", "Bearer MockedToken")
                .accept(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(match))
                .contentType(MediaType.APPLICATION_JSON);

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        // Assert
        String response = result.getResponse().getContentAsString();
        assertThat(response).isEqualTo("User is not available for this activity !");
    }

    @Test
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void userAcceptedSuccessfully() throws Exception {

        Activity training = amateurTraining;
        training.setApplicants(new ArrayList<>(Arrays.asList("Efe")));
        UserDTORequestModel model = new UserDTORequestModel(exampleUser, Position.COACH);

        training = mockActivityRepository.save(training);
        UUID id = training.getId();

        NotificationRequestModel notificationRequestModel = new NotificationRequestModel("Efe",
                NotificationStatus.ACCEPTED, id);


        mockServer.expect(requestTo("http://localhost:8082/notify"))
                .andExpect(method(HttpMethod.POST))
                .andExpect(content().json(JsonUtil.serialize(notificationRequestModel)))
                .andRespond(withSuccess("extra.efeunluyurt@gmail.com", MediaType.TEXT_PLAIN));

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/activity/" + id + "/accept")
                .header("Authorization", "Bearer MockedToken")
                .accept(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(model))
                .contentType(MediaType.APPLICATION_JSON);

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        mockServer.verify();
        // Assert
        String response = result.getResponse().getContentAsString();
        assertThat(response).isEqualTo("User " + "Efe"
                + " is accepted successfully");
    }

    @Test
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void activityDoesNotExist() throws Exception {

        Activity training = amateurTraining;
        training.setApplicants(new ArrayList<>(Arrays.asList("Alex", "Efe")));
        UserDTORequestModel model = new UserDTORequestModel(exampleUser, Position.COACH);

        UUID id = training.getId();

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/activity/" + id + "/accept")
                .header("Authorization", "Bearer MockedToken")
                .accept(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(model))
                .contentType(MediaType.APPLICATION_JSON);

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        // Assert
        String response = result.getResponse().getContentAsString();
        assertThat(response).isEqualTo("Activity does not exist!");
    }

    @Test
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void youAreNotTheOwner() throws Exception {

        Activity training = amateurTraining;
        training.setApplicants(new ArrayList<>(Arrays.asList("Alex", "Efe")));
        UserDTORequestModel model = new UserDTORequestModel(exampleUser, Position.COACH);

        training = mockActivityRepository.save(training);
        UUID id = training.getId();

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/activity/" + id + "/accept")
                .header("Authorization", "Bearer MockedToken")
                .accept(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(model))
                .contentType(MediaType.APPLICATION_JSON);

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        // Assert
        String response = result.getResponse().getContentAsString();
        assertThat(response).isEqualTo("Only the owner of the activity can accept users");
    }

    @Test
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void userIsAlreadyParticipating() throws Exception {

        Activity training = amateurTraining;
        training.setApplicants(new ArrayList<>(Arrays.asList("Alex", "Efe")));
        UserDTORequestModel model = new UserDTORequestModel(exampleUser, Position.COACH);

        training = mockActivityRepository.save(training);
        UUID id = training.getId();
        Match match1 = new Match(UUID.randomUUID(), id, "Efe", Position.COACH);

        mockMatchRepository.save(match1);

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/activity/" + id + "/accept")
                .header("Authorization", "Bearer MockedToken")
                .accept(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(model))
                .contentType(MediaType.APPLICATION_JSON);

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        // Assert
        String response = result.getResponse().getContentAsString();
        assertThat(response).isEqualTo("This user is already participating in the activity");
    }

    @Test
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void userDidNotApply() throws Exception {

        Activity training = amateurTraining;
        training.setApplicants(new ArrayList<>(Arrays.asList("Alex")));
        UserDTORequestModel model = new UserDTORequestModel(exampleUser, Position.COACH);
        training = mockActivityRepository.save(training);

        UUID id = training.getId();

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/activity/" + id + "/accept")
                .header("Authorization", "Bearer MockedToken")
                .accept(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(model))
                .contentType(MediaType.APPLICATION_JSON);

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        // Assert
        String response = result.getResponse().getContentAsString();
        assertThat(response).isEqualTo("This user didn't apply to this activity");
    }

    @Test
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void positionIsAlreadyFilled() throws Exception {

        Activity training = amateurTraining;
        training.setApplicants(new ArrayList<>(Arrays.asList("Alex", "Efe")));
        UserDTORequestModel model = new UserDTORequestModel(exampleUser, Position.COACH);
        training.setPositions(new ArrayList<>(Arrays.asList(Position.COX)));

        training = mockActivityRepository.save(training);
        UUID id = training.getId();

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/activity/" + id + "/accept")
                .header("Authorization", "Bearer MockedToken")
                .accept(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(model))
                .contentType(MediaType.APPLICATION_JSON);

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        // Assert
        String response = result.getResponse().getContentAsString();
        assertThat(response).isEqualTo("This position is already full");
    }

    @Test
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void activityIsFull() throws Exception {

        Activity training = amateurTraining;
        training.setApplicants(new ArrayList<>(Arrays.asList("Alex", "Efe")));
        UserDTORequestModel model = new UserDTORequestModel(exampleUser, Position.COACH);

        training = mockActivityRepository.save(training);
        UUID id = training.getId();

        NotificationRequestModel notificationRequestModel = new NotificationRequestModel("Efe",
                NotificationStatus.ACCEPTED, id);

        NotificationRequestModel notificationRequestModel2 = new NotificationRequestModel("Alex",
                NotificationStatus.ACTIVITY_FULL, id);

        mockServer.expect(requestTo("http://localhost:8082/notify"))
                .andExpect(method(HttpMethod.POST))
                .andExpect(content().json(JsonUtil.serialize(notificationRequestModel)))
                .andRespond(withSuccess("extra.efeunluyurt@gmail.com", MediaType.TEXT_PLAIN));

        mockServer.expect(requestTo("http://localhost:8082/notify"))
                .andExpect(method(HttpMethod.POST))
                .andExpect(content().json(JsonUtil.serialize(notificationRequestModel2)))
                .andRespond(withSuccess("extra.efeunluyurt@gmail.com", MediaType.TEXT_PLAIN));

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/activity/" + id + "/accept")
                .header("Authorization", "Bearer MockedToken")
                .accept(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(model))
                .contentType(MediaType.APPLICATION_JSON);

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        mockServer.verify();
        // Assert
        String response = result.getResponse().getContentAsString();
        assertThat(response).isEqualTo("User " + "Efe"
                + " is accepted successfully");
    }

    @Test
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void kickUser() throws Exception {

        Activity training = amateurTraining;
        training.setApplicants(new ArrayList<>(Arrays.asList("Alex", "Efe")));
        training = mockActivityRepository.save(training);
        UUID id = training.getId();

    }
}
