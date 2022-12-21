package rowing.user.controllers;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import rowing.commons.entities.UpdateUserDTO;
import rowing.commons.entities.UserDTO;
import rowing.user.authentication.AuthManager;
import rowing.user.domain.user.AvailabilityNotFoundException;
import rowing.user.domain.user.User;
import rowing.user.domain.user.UserRepository;
import rowing.user.domain.user.utils.UserNotFoundException;
import rowing.user.models.AvailabilityModel;
import rowing.user.models.TwoAvailabilitiesModel;
import rowing.user.services.AvailabilityService;
import rowing.user.services.UserService;

import java.time.DateTimeException;
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

    private final transient UserService userService;
    private final transient AvailabilityService availabilityService;

    /**
     * Instantiates a new controller.
     *
     * @param authManager Spring Security component used to authenticate and authorize the user
     */
    @Autowired
    public UserController(AuthManager authManager, UserRepository userRepository, UserService userService, AvailabilityService availabilityService) {
        this.authManager = authManager;
        this.userRepository = userRepository;
        this.userService = userService;
        this.availabilityService = availabilityService;
    }

    @PostMapping("/register-user")
    public ResponseEntity createUser(String userId) {
        User user = new User(userId);
        userRepository.save(user);
        return ResponseEntity.ok().build();
    }

    /**
     * Gets user.
     *
     * @return user
     */
    @GetMapping("/get-user")
    public ResponseEntity<UserDTO> getUser() {
        String userId = authManager.getUsername();
        UserDTO userDTO;

        try{
            userDTO = userService.getUser(userId);
        } catch (UserNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found", e);
        }

        return ResponseEntity.ok(userDTO);
    }


    /**
     * Gets user email.
     *
     * @return the email of the user
     */
    @GetMapping("/get-email-address")
    public ResponseEntity<String> getEmailAddress(@RequestBody String username) {
        JSONArray array = new JSONArray("[" + username + "]");
        String userId = (String) ((JSONObject) array.get(0)).get("username");
        Optional<User> u = userRepository.findByUserId(userId);
        System.out.println(username);
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
    public ResponseEntity<UserDTO> updateUser(@RequestBody UpdateUserDTO updateUserDTO) {
        String userId = authManager.getUsername();

        UserDTO updatedUserDTO;

        try {
            updatedUserDTO = userService.updateUser(userId, updateUserDTO);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Input types incorrect.");
        } catch (UserNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found.");
        }


        return ResponseEntity.ok(updatedUserDTO);
    }

    /**
     * Add specified availability to the user with userId.
     *
     * @param request with the desired availability
     * @return 200 OK if the userId and adding is successful
     * @throws Exception if the userId doesn't exist or availability is not in the correct format
     */
    @PostMapping("/add-availability")
    public ResponseEntity addAvailability(@RequestBody AvailabilityModel request) {
        String userId = authManager.getUsername();
        try {
            User u = availabilityService.addAvailability(request.getDay(), request.getStart(),
                    request.getEnd(), userId);
            userRepository.save(u);
        } catch (IllegalArgumentException | DateTimeException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "AVAILABILITY IS NOT IN THE CORRECT FORMAT", e);
        } catch (UserNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "USER NOT FOUND", e);
        }
        return ResponseEntity.ok().build();
    }

    /**
     * Remove specified availability from the user with userId.
     *
     * @param request with the desired availability
     * @return 200 OK if the userId and removing is successful
     * @throws Exception if the userId doesn't exist or availability is not in the correct format or doesn't exist
     */
    @PostMapping("/remove-availability")
    public ResponseEntity removeAvailability(@RequestBody AvailabilityModel request) {
        String userId = authManager.getUsername();
        try {
            User u = availabilityService.removeAvailability(request.getDay(), request.getStart(),
                    request.getEnd(), userId);
            userRepository.save(u);
        } catch (AvailabilityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "AVAILABILITY NOT FOUND", e);
        } catch (IllegalArgumentException | DateTimeException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "AVAILABILITY IS NOT IN THE CORRECT FORMAT");
        } catch (UserNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "USER NOT FOUND", e);
        }
        return ResponseEntity.ok().build();
    }

    /**
     * /**
     * Edits the specified availability, by removing the old one and inserting the new one.
     *
     * @param intervals A model that stores two availability intervals which correspond to the old and new one.
     * @return 200 OK if the userId and editing is successful
     * @throws Exception if the userId doesn't exist or availability is not in the correct format or doesn't exist
     */
    @PostMapping(value = "/edit-availability")
    public ResponseEntity editAvailability(@RequestBody TwoAvailabilitiesModel intervals) {
        String userId = authManager.getUsername();
        AvailabilityModel oldInterval = intervals.getOldAvailability();
        AvailabilityModel newInterval = intervals.getNewAvailability();
        try {
            //System.out.println(oldInterval);
            //System.out.println(newInterval);
            User u = availabilityService
                    .editAvailability(oldInterval.getDay(), oldInterval.getStart(), oldInterval.getEnd(),
                            newInterval.getDay(), newInterval.getStart(), newInterval.getEnd(), userId);
            userRepository.save(u);
        } catch (AvailabilityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "AVAILABILITY NOT FOUND OR CANNOT BE REPLACED", e);
        } catch (IllegalArgumentException | DateTimeException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "AVAILABILITY IS NOT IN THE CORRECT FORMAT", e);
        } catch (UserNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "USER NOT FOUND", e);
        }
        return ResponseEntity.ok().build();
    }

}
