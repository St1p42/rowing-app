package rowing.user.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyObject;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import rowing.user.authentication.AuthManager;
import rowing.user.authentication.JwtTokenVerifier;
import rowing.user.domain.user.AvailabilityIntervals;
import rowing.user.domain.user.Gender;
import rowing.user.domain.user.User;
import rowing.user.domain.user.UserRepository;
import rowing.user.models.AvailabilityModel;
import rowing.user.models.TwoAvailabilitiesModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;


@SpringBootTest
@ExtendWith(SpringExtension.class)
// activate profiles to have spring use mocks during auto-injection of certain beans.
@ActiveProfiles({"test", "mockTokenVerifier", "mockAuthenticationManager"})
//@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@AutoConfigureMockMvc
public class UserTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private transient JwtTokenVerifier mockJwtTokenVerifier;

    @Autowired
    private transient AuthManager mockAuthenticationManager;

    @MockBean
    private transient UserRepository userRepository;

    @Test
    public void testAddAvailability() throws Exception {
        // Arrange
        // Notice how some custom parts of authorisation need to be mocked.
        // Otherwise, the integration test would never be able to authorise as the authorisation server is offline.
        when(mockAuthenticationManager.getUsername()).thenReturn("bogdan");
        when(mockJwtTokenVerifier.validateToken(anyString())).thenReturn(true);
        when(mockJwtTokenVerifier.getUsernameFromToken(anyString())).thenReturn("bogdan");
        when(userRepository.findByUserId("bogdan")).thenReturn(
                Optional.of(new User("bogdan", "bogdan", "bogdan", "bogdan@gmail.com")));
        //when(userRepository.save(Mockito.any(User.class))).thenAnswer(i -> i.getArguments());
        // Act
        // Still include Bearer token as AuthFilter itself is not mocked
        ObjectMapper objectMapper = new ObjectMapper().registerModules(new Jdk8Module(), new JavaTimeModule());
        RequestBuilder request = MockMvcRequestBuilders
                    .post("/add-availability")
                    .header("Authorization", "Bearer MockedToken")
                    .accept(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(
                            new AvailabilityModel("monday", "10:00", "11:00")))
                    .contentType(MediaType.APPLICATION_JSON);

        ResultActions result = mockMvc.perform(request);

        // Assert
        result.andExpect(status().isOk());
    }

    @Test
    public void testAddAvailabilityException() throws Exception {
        // Arrange
        // Notice how some custom parts of authorisation need to be mocked.
        // Otherwise, the integration test would never be able to authorise as the authorisation server is offline.
        when(mockAuthenticationManager.getUsername()).thenReturn("bogdan");
        when(mockJwtTokenVerifier.validateToken(anyString())).thenReturn(true);
        when(mockJwtTokenVerifier.getUsernameFromToken(anyString())).thenReturn("bogdan");
        when(userRepository.findByUserId("bogdan")).thenReturn(
                Optional.of(new User("bogdan", "bogdan", "bogdan", "bogdan@gmail.com")));
        //when(userRepository.save(Mockito.any(User.class))).thenAnswer(i -> i.getArguments());
        // Act
        // Still include Bearer token as AuthFilter itself is not mocked
        ObjectMapper objectMapper = new ObjectMapper().registerModules(new Jdk8Module(), new JavaTimeModule());
        RequestBuilder request = MockMvcRequestBuilders
                .post("/add-availability")
                .header("Authorization", "Bearer MockedToken")
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(
                        new AvailabilityModel("monday", "12:00", "11:00")))
                .contentType(MediaType.APPLICATION_JSON);

        ResultActions result = mockMvc.perform(request);

        // Assert
        result.andExpect(status().isBadRequest());
    }

    @Test
    public void testRemoveAvailability() throws Exception {
        // Arrange
        // Notice how some custom parts of authorisation need to be mocked.
        // Otherwise, the integration test would never be able to authorise as the authorisation server is offline.
        User u = new User("bogdan", "bogdan", "bogdan", "bogdan@gmail.com");
        u.setAvailability(new ArrayList<AvailabilityIntervals>(
                Arrays.asList(new AvailabilityIntervals("monday", "10:00", "11:00"))));
        when(mockAuthenticationManager.getUsername()).thenReturn("bogdan");
        when(mockJwtTokenVerifier.validateToken(anyString())).thenReturn(true);
        when(mockJwtTokenVerifier.getUsernameFromToken(anyString())).thenReturn("bogdan");
        when(userRepository.findByUserId("bogdan")).thenReturn(Optional.of(u));
        //when(userRepository.save(Mockito.any(User.class))).thenAnswer(i -> i.getArguments());
        // Act
        // Still include Bearer token as AuthFilter itself is not mocked
        ObjectMapper objectMapper = new ObjectMapper().registerModules(new Jdk8Module(), new JavaTimeModule());
        RequestBuilder request = MockMvcRequestBuilders
                .post("/remove-availability")
                .header("Authorization", "Bearer MockedToken")
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(
                        new AvailabilityModel("monday", "10:00", "11:00")))
                .contentType(MediaType.APPLICATION_JSON);

        ResultActions result = mockMvc.perform(request);

        // Assert
        result.andExpect(status().isOk());
    }

    @Test
    public void testRemoveAvailabilityException1() throws Exception {
        // Arrange
        // Notice how some custom parts of authorisation need to be mocked.
        // Otherwise, the integration test would never be able to authorise as the authorisation server is offline.
        User u = new User("bogdan", "bogdan", "bogdan", "bogdan@gmail.com");
        u.setAvailability(new ArrayList<AvailabilityIntervals>(
                Arrays.asList(new AvailabilityIntervals("tuesday", "10:00", "11:00"))));
        when(mockAuthenticationManager.getUsername()).thenReturn("bogdan");
        when(mockJwtTokenVerifier.validateToken(anyString())).thenReturn(true);
        when(mockJwtTokenVerifier.getUsernameFromToken(anyString())).thenReturn("bogdan");
        when(userRepository.findByUserId("bogdan")).thenReturn(Optional.of(u));
        //when(userRepository.save(Mockito.any(User.class))).thenAnswer(i -> i.getArguments());
        // Act
        // Still include Bearer token as AuthFilter itself is not mocked
        ObjectMapper objectMapper = new ObjectMapper().registerModules(new Jdk8Module(), new JavaTimeModule());
        RequestBuilder request = MockMvcRequestBuilders
                .post("/remove-availability")
                .header("Authorization", "Bearer MockedToken")
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(
                        new AvailabilityModel("monday", "10:00", "11:00")))
                .contentType(MediaType.APPLICATION_JSON);

        ResultActions result = mockMvc.perform(request);

        // Assert
        result.andExpect(status().isNotFound());
    }

    @Test
    public void testRemoveAvailabilityException2() throws Exception {
        // Arrange
        // Notice how some custom parts of authorisation need to be mocked.
        // Otherwise, the integration test would never be able to authorise as the authorisation server is offline.
        User u = new User("bogdan", "bogdan", "bogdan", "bogdan@gmail.com");
        u.setAvailability(new ArrayList<AvailabilityIntervals>(
                Arrays.asList(new AvailabilityIntervals("monday", "10:00", "11:00"))));
        when(mockAuthenticationManager.getUsername()).thenReturn("bogdan");
        when(mockJwtTokenVerifier.validateToken(anyString())).thenReturn(true);
        when(mockJwtTokenVerifier.getUsernameFromToken(anyString())).thenReturn("bogdan");
        when(userRepository.findByUserId("bogdan")).thenReturn(Optional.of(u));
        //when(userRepository.save(Mockito.any(User.class))).thenAnswer(i -> i.getArguments());
        // Act
        // Still include Bearer token as AuthFilter itself is not mocked
        ObjectMapper objectMapper = new ObjectMapper().registerModules(new Jdk8Module(), new JavaTimeModule());
        RequestBuilder request = MockMvcRequestBuilders
                .post("/remove-availability")
                .header("Authorization", "Bearer MockedToken")
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(
                        new AvailabilityModel("monday", "12:00", "11:00")))
                .contentType(MediaType.APPLICATION_JSON);

        ResultActions result = mockMvc.perform(request);

        // Assert
        result.andExpect(status().isBadRequest());
    }

    @Test
    public void testEditAvailability() throws Exception {
        // Arrange
        // Notice how some custom parts of authorisation need to be mocked.
        // Otherwise, the integration test would never be able to authorise as the authorisation server is offline.
        User u = new User("bogdan", "bogdan", "bogdan", "bogdan@gmail.com");
        u.setAvailability(new ArrayList<AvailabilityIntervals>(
                Arrays.asList(new AvailabilityIntervals("tuesday", "10:00", "11:00"))));
        when(mockAuthenticationManager.getUsername()).thenReturn("bogdan");
        when(mockJwtTokenVerifier.validateToken(anyString())).thenReturn(true);
        when(mockJwtTokenVerifier.getUsernameFromToken(anyString())).thenReturn("bogdan");
        when(userRepository.findByUserId("bogdan")).thenReturn(Optional.of(u));
        //when(userRepository.save(Mockito.any(User.class))).thenAnswer(i -> i.getArguments());
        // Act
        // Still include Bearer token as AuthFilter itself is not mocked
        ObjectMapper objectMapper = new ObjectMapper().registerModules(new Jdk8Module(), new JavaTimeModule());
        RequestBuilder request = MockMvcRequestBuilders
                .post("/edit-availability")
                .header("Authorization", "Bearer MockedToken")
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new TwoAvailabilitiesModel(
                        new AvailabilityModel("tuesday", "10:00", "11:00"),
                        new AvailabilityModel("monday", "12:00", "13:00"))))
                .contentType(MediaType.APPLICATION_JSON);
        ResultActions result = mockMvc.perform(request);

        // Assert
        result.andExpect(status().isOk());
    }

    @Test
    public void testEditAvailabilityException1() throws Exception {
        // Arrange
        // Notice how some custom parts of authorisation need to be mocked.
        // Otherwise, the integration test would never be able to authorise as the authorisation server is offline.
        User u = new User("bogdan", "bogdan", "bogdan", "bogdan@gmail.com");
        u.setAvailability(new ArrayList<AvailabilityIntervals>(
                Arrays.asList(new AvailabilityIntervals("tuesday", "10:00", "11:00"))));
        when(mockAuthenticationManager.getUsername()).thenReturn("bogdan");
        when(mockJwtTokenVerifier.validateToken(anyString())).thenReturn(true);
        when(mockJwtTokenVerifier.getUsernameFromToken(anyString())).thenReturn("bogdan");
        when(userRepository.findByUserId("bogdan")).thenReturn(Optional.of(u));
        //when(userRepository.save(Mockito.any(User.class))).thenAnswer(i -> i.getArguments());
        // Act
        // Still include Bearer token as AuthFilter itself is not mocked
        ObjectMapper objectMapper = new ObjectMapper().registerModules(new Jdk8Module(), new JavaTimeModule());
        RequestBuilder request = MockMvcRequestBuilders
                .post("/edit-availability")
                .header("Authorization", "Bearer MockedToken")
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new TwoAvailabilitiesModel(
                        new AvailabilityModel("wednesday", "10:00", "11:00"),
                        new AvailabilityModel("monday", "12:00", "13:00"))))
                .contentType(MediaType.APPLICATION_JSON);
        ResultActions result = mockMvc.perform(request);

        // Assert
        result.andExpect(status().isNotFound());
    }

    @Test
    public void testEditAvailabilityException2() throws Exception {
        // Arrange
        // Notice how some custom parts of authorisation need to be mocked.
        // Otherwise, the integration test would never be able to authorise as the authorisation server is offline.
        User u = new User("bogdan", "bogdan", "bogdan", "bogdan@gmail.com");
        u.setAvailability(new ArrayList<AvailabilityIntervals>(
                Arrays.asList(new AvailabilityIntervals("tuesday", "10:00", "11:00"))));
        when(mockAuthenticationManager.getUsername()).thenReturn("bogdan");
        when(mockJwtTokenVerifier.validateToken(anyString())).thenReturn(true);
        when(mockJwtTokenVerifier.getUsernameFromToken(anyString())).thenReturn("bogdan");
        when(userRepository.findByUserId("bogdan")).thenReturn(Optional.of(u));
        //when(userRepository.save(Mockito.any(User.class))).thenAnswer(i -> i.getArguments());
        // Act
        // Still include Bearer token as AuthFilter itself is not mocked
        ObjectMapper objectMapper = new ObjectMapper().registerModules(new Jdk8Module(), new JavaTimeModule());
        RequestBuilder request = MockMvcRequestBuilders
                .post("/edit-availability")
                .header("Authorization", "Bearer MockedToken")
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new TwoAvailabilitiesModel(
                        new AvailabilityModel("tuesday", "10:00", "11:00"),
                        new AvailabilityModel("monday", "15:00", "13:00"))))
                .contentType(MediaType.APPLICATION_JSON);
        ResultActions result = mockMvc.perform(request);

        // Assert
        result.andExpect(status().isBadRequest());
    }
}
