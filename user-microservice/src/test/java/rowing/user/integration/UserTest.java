package rowing.user.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import rowing.user.authentication.AuthManager;
import rowing.user.authentication.JwtTokenVerifier;
import rowing.user.domain.user.UpdateUserDTO;
import rowing.user.domain.user.User;
import rowing.user.domain.user.UserRepository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


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
    private transient UserRepository mockUserRepository;


    @Test
    public void getUser() throws Exception {
        User originalUser = new User("bogdan", "lala", "lala", "bogdan@gmail.com");

        // Arrange
        // Notice how some custom parts of authorisation need to be mocked.
        // Otherwise, the integration test would never be able to authorise as the authorisation server is offline.
        when(mockAuthenticationManager.getUsername()).thenReturn("bogdan");
        when(mockJwtTokenVerifier.validateToken(anyString())).thenReturn(true);
        when(mockJwtTokenVerifier.getUsernameFromToken(anyString())).thenReturn("bogdan");
        when(mockUserRepository.findByUserId("bogdan")).thenReturn(Optional.of(originalUser));

        // Act
        // Still include Bearer token as AuthFilter itself is not mocked
        ResultActions result = mockMvc.perform(get("/user/get-user")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer MockedToken"));

        // Assert
        result.andExpect(status().isOk());
        String response = result.andReturn().getResponse().getContentAsString();

        assertThat(response).isEqualTo(new ObjectMapper().writeValueAsString(originalUser));
    }


    @Test
    public void patchUserNoChanges() throws Exception {
        User originalUser = new User("bogdan", "lala", "lala", "bogdan@gmail.com");

        // Arrange
        // Notice how some custom parts of authorisation need to be mocked.
        // Otherwise, the integration test would never be able to authorise as the authorisation server is offline.
        when(mockAuthenticationManager.getUsername()).thenReturn("bogdan");
        when(mockJwtTokenVerifier.validateToken(anyString())).thenReturn(true);
        when(mockJwtTokenVerifier.getUsernameFromToken(anyString())).thenReturn("bogdan");
        when(mockUserRepository.findByUserId("bogdan")).thenReturn(Optional.of(originalUser));

        UpdateUserDTO updateUserDTO = new UpdateUserDTO(null, null, null, null, null, null, null, null, null);

        // Act
        // Still include Bearer token as AuthFilter itself is not mocked
        ResultActions result = mockMvc.perform(patch("/user/update-user")
                .content(new ObjectMapper().writeValueAsString(updateUserDTO))
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer MockedToken"));


        // Assert
        result.andExpect(status().isOk());
        String response = result.andReturn().getResponse().getContentAsString();

        assertThat(response).isEqualTo(new ObjectMapper().writeValueAsString(originalUser));
    }

    @Test
    public void patchUserChanges() throws Exception {
        User originalUser = new User("bogdan", "lala", "lala", "bogdan@gmail.com");

        // Arrange
        // Notice how some custom parts of authorisation need to be mocked.
        // Otherwise, the integration test would never be able to authorise as the authorisation server is offline.
        when(mockAuthenticationManager.getUsername()).thenReturn("bogdan");
        when(mockJwtTokenVerifier.validateToken(anyString())).thenReturn(true);
        when(mockJwtTokenVerifier.getUsernameFromToken(anyString())).thenReturn("bogdan");
        when(mockUserRepository.findByUserId("bogdan")).thenReturn(Optional.of(originalUser));

        UpdateUserDTO updateUserDTO = new UpdateUserDTO(null, null, "joyce@gmail.com", null, null, null, null, null, null);
        User shouldBeUpdatedToThisUser = new User("bogdan", "lala", "lala", "joyce@gmail.com");

        // Act
        // Still include Bearer token as AuthFilter itself is not mocked
        ResultActions result = mockMvc.perform(patch("/user/update-user")
                .content(new ObjectMapper().writeValueAsString(updateUserDTO))
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer MockedToken"));


        // Assert
        result.andExpect(status().isOk());
        String response = result.andReturn().getResponse().getContentAsString();

        assertThat(response).isEqualTo(new ObjectMapper().writeValueAsString(shouldBeUpdatedToThisUser));
    }


    @Test
    public void getUserEmail() throws Exception {
        // Arrange
        // Notice how some custom parts of authorisation need to be mocked.
        // Otherwise, the integration test would never be able to authorise as the authorisation server is offline.
        when(mockAuthenticationManager.getUsername()).thenReturn("bogdan");
        when(mockJwtTokenVerifier.validateToken(anyString())).thenReturn(true);
        when(mockJwtTokenVerifier.getUsernameFromToken(anyString())).thenReturn("bogdan");
        when(mockUserRepository.findByUserId("bogdan"))
                .thenReturn(Optional.of(new User("bogdan", "lala",
                        "lala", "bogdan@gmail.com")));
        // Act
        // Still include Bearer token as AuthFilter itself is not mocked
        ResultActions result = mockMvc.perform(get("/user/get-email-address")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer MockedToken"));

        // Assert
        result.andExpect(status().isOk());

        String response = result.andReturn().getResponse().getContentAsString();

        assertThat(response).isEqualTo("bogdan@gmail.com");

    }

    @Test
    public void getUserEmailException() throws Exception {
        // Arrange
        // Notice how some custom parts of authorisation need to be mocked.
        // Otherwise, the integration test would never be able to authorise as the authorisation server is offline.
        when(mockAuthenticationManager.getUsername()).thenReturn("bogdan");
        when(mockJwtTokenVerifier.validateToken(anyString())).thenReturn(true);
        when(mockJwtTokenVerifier.getUsernameFromToken(anyString())).thenReturn("bogdan");
        when(mockUserRepository.findByUserId("bogdan")).thenReturn(Optional.empty());
        // Act
        // Still include Bearer token as AuthFilter itself is not mocked
        ResultActions result = mockMvc.perform(get("/user/get-email-address")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer MockedToken"));

        // Assert
        result.andExpect(status().isNotFound());
        String response = result.andReturn().getResponse().getErrorMessage();

        assertThat(response).isEqualTo("User not found");

    }
}
