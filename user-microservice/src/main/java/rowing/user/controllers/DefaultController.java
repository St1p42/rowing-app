package rowing.user.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import rowing.user.authentication.AuthManager;
import rowing.user.domain.user.User;
import rowing.user.domain.user.UserRepository;

import java.util.Optional;

/**
 * Hello World example controller.
 * <p>
 * This controller shows how you can extract information from the JWT token.
 * </p>
 */
@RestController
public class DefaultController {

    private final transient AuthManager authManager;

    private final transient UserRepository userRepository;

    /**
     * Instantiates a new controller.
     *
     * @param authManager Spring Security component used to authenticate and authorize the user
     */
    @Autowired
    public DefaultController(AuthManager authManager, UserRepository userRepository) {
        this.authManager = authManager;
        this.userRepository = userRepository;
    }

    /**
     * Gets example by id.
     *
     * @return the example found in the database with the given id
     */
    @GetMapping("/hello")
    public ResponseEntity<String> helloWorld() {
        return ResponseEntity.ok("Hello " + authManager.getUsername());

    }

    /**
     * Gets user email.
     *
     * @return the email of the user
     */
    @GetMapping("/get-email-address")
    public ResponseEntity<String> getEmailAddress() {
        String userId = authManager.getUsername();
        Optional<User> u = userRepository.findByUserId(userId);
        if (!u.isPresent()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }
        User user = u.get();
        return ResponseEntity.ok(user.getEmail());
    }

    /**
     * Updates basic user details :
     * rowing positions, availability, cox certificates
     *
     * @param basicUserDetails User object containing updated details
     *
     * @return updated user
     */
    @PutMapping("{userId}")
    public ResponseEntity<User> createBasicUserDetails(@RequestBody User basicUserDetails){
        String userId = authManager.getUsername();
        Optional<User> u = userRepository.findByUserId(userId);
        if (!u.isPresent()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }
        User updateUser = u.get();

        updateUser.createProfileBasic(basicUserDetails.getRowingPositions(),
                basicUserDetails.getAvailability(),
                basicUserDetails.getCoxCertificates());

        userRepository.save(updateUser);

        return ResponseEntity.ok(updateUser);
    }
}
