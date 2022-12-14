package rowing.user.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import rowing.user.authentication.AuthManager;
import rowing.user.domain.user.AvailabilityIntervals;
import rowing.user.domain.user.AvailabilityNotFoundException;
import rowing.user.domain.user.User;
import rowing.user.domain.user.UserRepository;
import rowing.user.models.AvailabilityModel;
import rowing.user.models.TwoAvailabilitiesModel;
import rowing.user.services.AvailabilityService;

import java.time.DateTimeException;
import java.util.Date;
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

    @Autowired
    private AvailabilityService availabilityService;

    @Autowired
    private UserRepository userRepository;

    /**
     * Instantiates a new controller.
     *
     * @param authManager Spring Security component used to authenticate and authorize the user
     */
    @Autowired
    public DefaultController(AuthManager authManager, AvailabilityService availabilityService,
                             UserRepository userRepository) {
        this.authManager = authManager;
        this.availabilityService = availabilityService;
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
     * Add user template. (needs to be replaced by better implementation).
     *
     * @param user - user to be added to the database
     * @return the user that was added in the database
     */
    @PostMapping("/addUser")
    public ResponseEntity<User> addUser(@RequestBody User user) {
        user.setUserId(authManager.getUsername());
        try {
            System.out.println(user);
            //System.out.println(user.getAvailability().get(0).toString());
            userRepository.save(user);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "INCORRECT DETAILS", e);
        }
        return ResponseEntity.ok(user);
    }

    /**
     * View details of user template. (needs to be replaced by better implementation).
     *
     * @param userId - identifying the user to get details for
     * @return user with the required id.
     */
    @GetMapping("/{userId}/viewDetails")
    public ResponseEntity<User> getUserDetails(@PathVariable("userId") String userId) {
        Optional<User> u = userRepository.findByUserId(userId);
        if (u.isPresent()) {
            return ResponseEntity.ok(u.get());
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "USER NOT FOUND");
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
        }
        return ResponseEntity.ok().build();
    }
}
