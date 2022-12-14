package rowing.user.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import rowing.user.authentication.AuthManager;
import rowing.user.domain.user.UpdateUserDTO;
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
@RequestMapping("/user")
public class UserController {

    private final transient AuthManager authManager;

    private final transient UserRepository userRepository;

    /**
     * Instantiates a new controller.
     *
     * @param authManager Spring Security component used to authenticate and authorize the user
     */
    @Autowired
    public UserController(AuthManager authManager, UserRepository userRepository) {
        this.authManager = authManager;
        this.userRepository = userRepository;
    }

    /**
     * Gets user.
     *
     * @return user
     */
    @GetMapping("/get-user")
    public ResponseEntity<User> getUser() {
        String userId = authManager.getUsername();
        Optional<User> optionalUser = userRepository.findByUserId(userId);

        if (!optionalUser.isPresent()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }

        User user = optionalUser.get();
        return ResponseEntity.ok(user);
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
     * Updates all user details.
     * If null fields are given for some fields, the old values are kept.
     *
     * @param updateUserDTO DTO object containing updated user details
     *
     * @return updated user
     */
    @PatchMapping("/update-user")
    public ResponseEntity<User> updateUser(@RequestBody UpdateUserDTO updateUserDTO) {
        String userId = authManager.getUsername();
        Optional<User> optionalUser = userRepository.findByUserId(userId);

        if (optionalUser.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }

        User user = optionalUser.get();

        Optional.ofNullable(updateUserDTO.getRowingPositions()).ifPresent(user::setRowingPositions);
        Optional.ofNullable(updateUserDTO.getAvailability()).ifPresent(user::setAvailability);
        Optional.ofNullable(updateUserDTO.getEmail()).ifPresent(user::setEmail);
        Optional.ofNullable(updateUserDTO.getFirstName()).ifPresent(user::setFirstName);
        Optional.ofNullable(updateUserDTO.getLastName()).ifPresent(user::setLastName);
        Optional.ofNullable(updateUserDTO.getCoxCertificates()).ifPresent(user::setCoxCertificates);
        Optional.ofNullable(updateUserDTO.getGender()).ifPresent(user::setGender);
        Optional.ofNullable(updateUserDTO.getRowingOrganization()).ifPresent(user::setRowingOrganization);
        Optional.ofNullable(updateUserDTO.getCompetitive()).ifPresent(user::setCompetitive);

        userRepository.save(user);

        return ResponseEntity.ok(user);
    }

}
