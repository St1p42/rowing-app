package rowing.activity.integration;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.SerializationFeature;
import org.junit.jupiter.api.BeforeEach;
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
import rowing.commons.Position;
import rowing.commons.entities.ActivityDTO;
import rowing.commons.entities.MatchingDTO;
//import com.fasterxml.jackson.*;

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

    @MockBean
    private transient MatchRepository mockMatchRepository;

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
        when(mockAuthenticationManager.getNetId()).thenReturn("ExampleUser");
        when(mockJwtTokenVerifier.validateToken(anyString())).thenReturn(true);
        when(mockJwtTokenVerifier.getNetIdFromToken(anyString())).thenReturn("ExampleUser");
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
        positionList.add(Position.COX);
        List<String> applicantList = new ArrayList<>();

        director.constructTraining((TrainingBuilder) trainingBuilder, UUID.randomUUID(),
                "Admin", "Amateur Training", "Training",
                amateurTrainingDate, positionList, applicantList);
        amateurTraining = trainingBuilder.build();

        Builder competitionBuilder = new CompetitionBuilder();
        director.constructCompetition((CompetitionBuilder) competitionBuilder, UUID.randomUUID(),
                "Admin", "Amateur Competition", "Competition",
                amateurCompetitionDate,  Gender.MALE, "TUDelft", positionList, applicantList);
        amateurCompetition = competitionBuilder.build();

        availability = new ArrayList<AvailabilityIntervals>();
        availability.add(new AvailabilityIntervals("wednesday", "14:05", "14:06"));
        availability.add(new AvailabilityIntervals("thursday", "16:05", "16:06"));
        match = new MatchingDTO(UUID.randomUUID(), null,
                "Admin", Position.COX, Gender.MALE, true, "TUDelft",
                availability, null);
    }

    @Test
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

        List<Activity> activityList = new ArrayList<>();
        List<ActivityDTO> activityDTOList = new ArrayList<>();

        activityList.add(amateurTraining);
        activityList.add(activity);
        activityDTOList.add(amateurTraining.toDto());
        activityDTOList.add(activity.toDto());
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
                mapper.writeValueAsString(activityDTOList), false);
    }

    @Test
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

        List<Activity> activityList = new ArrayList<>();
        List<ActivityDTO> activityDTOList = new ArrayList<>();

        activityList.add(amateurTraining);
        activityList.add(activity);
        activityDTOList.add(amateurTraining.toDto());
        when(mockActivityRepository.findAll()).thenReturn(activityList);

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
    public void deleteActivity() throws Exception {
        //Create a new activity
        List<Position> positionList = new ArrayList<>();
        positionList.add(Position.COACH);
        positionList.add(Position.COX);
        amateurTraining.setPositions(positionList);
        when(mockActivityRepository.findActivityById(trainingId)).thenReturn(Optional.of(amateurTraining));

        ResultActions result = mockMvc.perform(get("/activity/" + trainingId + "/delete")
                .header("Authorization", "Bearer MockedToken").contentType(MediaType.APPLICATION_JSON));

        result.andExpect(status().isOk());
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);

        String response = result.andReturn().getResponse().getContentAsString();

        JSONAssert.assertEquals(response, mapper.writeValueAsString(amateurTraining.toDto()), false);
    }

    @Test
    public void deleteActivityException() throws Exception {
        when(mockActivityRepository.findActivityById(trainingId)).thenReturn(Optional.of(amateurTraining));

        UUID id2 = UUID.randomUUID();
        ResultActions result = mockMvc.perform(get("/activity/" + id2 + "/delete")
                .header("Authorization", "Bearer MockedToken").contentType(MediaType.APPLICATION_JSON));

        result.andExpect(status().isNotFound());
    }

    @Test
    public void testSignUpTraining() throws Exception {

        match.setActivityId(trainingId); // Make sure to set for the activity you want to sign up for
        when(mockActivityRepository.findActivityById(trainingId)).thenReturn(Optional.of(amateurTraining));

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
    public void testSignUpCompetition() throws Exception {
        match.setActivityId(competitionId);
        when(mockActivityRepository.findActivityById(competitionId)).thenReturn(Optional.of(amateurCompetition));

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
    public void testSignUpCompetitionGenderException() throws Exception {
        match.setActivityId(competitionId);
        match.setGender(Gender.FEMALE);
        when(mockActivityRepository.findActivityById(competitionId)).thenReturn(Optional.of(amateurCompetition));

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
    public void testSignUpCompetitionOrganisationException() throws Exception {
        match.setActivityId(competitionId);
        match.setOrganisation("TUEindhoven");
        when(mockActivityRepository.findActivityById(competitionId)).thenReturn(Optional.of(amateurCompetition));

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
    public void testSignUpCompetitionCompetitiveException() throws Exception {
        match.setActivityId(competitionId);
        match.setCompetitive(false);
        when(mockActivityRepository.findActivityById(competitionId)).thenReturn(Optional.of(amateurCompetition));

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
    public void testSignUpAvailabilityException() throws Exception {
        match.setActivityId(competitionId);
        List<AvailabilityIntervals> newAvailability = new ArrayList<AvailabilityIntervals>();
        newAvailability.add(new AvailabilityIntervals("monday", "12:00", "12:05"));
        match.setAvailability(newAvailability);
        when(mockActivityRepository.findActivityById(competitionId)).thenReturn(Optional.of(amateurCompetition));

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
}
