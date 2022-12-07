package rowing.authentication.domain.user;

import org.springframework.stereotype.Service;

/**
 * A DDD service for registering a new user.
 */
@Service
public class RegistrationService {
    private final transient CredentialRepository credentialRepository;
    private final transient PasswordHashingService passwordHashingService;

    /**
     * Instantiates a new UserService.
     *
     * @param credentialRepository  the user repository
     * @param passwordHashingService the password encoder
     */
    public RegistrationService(CredentialRepository credentialRepository, PasswordHashingService passwordHashingService) {
        this.credentialRepository = credentialRepository;
        this.passwordHashingService = passwordHashingService;
    }

    /**
     * Register a new user.
     *
     * @param username    The Username of the user
     * @param password The password of the user
     * @throws Exception if the user already exists
     */
    public AppUser registerUser(Username username, Password password) throws Exception {

        if (checkUsernameIsUnique(username)) {
            // Hash password
            HashedPassword hashedPassword = passwordHashingService.hash(password);

            // Create new account
            AppUser user = new AppUser(username, hashedPassword);
            credentialRepository.save(user);

            return user;
        }

        throw new UsernameAlreadyInUseException(username);
    }

    public boolean checkUsernameIsUnique(Username username) {
        return !credentialRepository.existsByUsername(username);
    }
}
