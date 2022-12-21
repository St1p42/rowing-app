package rowing.authentication.domain.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * A DDD service for registering a new user.
 */
@Service
public class RegistrationService {
    @Value("${uri.users.url}")
    private String url;

    @Value("${uri.users.port}")
    private String port;

    @Value("${uri.users.getregistrationpath}")
    private String registrationPath;

    @Autowired
    RestTemplate restTemplate;
    private final transient CredentialRepository credentialRepository;
    private final transient PasswordHashingService passwordHashingService;

    /**
     * Instantiates a new UserService.
     *
     * @param credentialRepository   the user repository
     * @param passwordHashingService the password encoder
     */
    public RegistrationService(CredentialRepository credentialRepository, PasswordHashingService passwordHashingService) {
        this.credentialRepository = credentialRepository;
        this.passwordHashingService = passwordHashingService;
    }

    /**
     * Register a new user.
     *
     * @param username The Username of the user
     * @param password The password of the user
     * @throws Exception if the user already exists
     */
    public AppUser registerUser(Username username, Password password) throws Exception {

        if (!checkUsernameIsUnique(username)) {
            throw new UsernameAlreadyInUseException(username);
        }

        // Hash password
        HashedPassword hashedPassword = passwordHashingService.hash(password);

        // Create new account
        AppUser user = new AppUser(username, hashedPassword);
        credentialRepository.save(user);

        //building the request
        String uri = url + ":" + port + registrationPath;
        HttpHeaders headers = new HttpHeaders();
        String body = "{\"userId\":\"" + user.getUsername() + "\"}";
        HttpEntity requestHttp = new HttpEntity(body, headers);

        //sending the request
        ResponseEntity<String> response = restTemplate.exchange(uri, HttpMethod.POST, requestHttp, String.class);

        return user;

    }

    public boolean checkUsernameIsUnique(Username username) {
        return !credentialRepository.existsByUsername(username);
    }
}
