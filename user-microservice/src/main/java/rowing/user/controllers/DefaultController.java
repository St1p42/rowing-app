package rowing.user.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import rowing.user.authentication.AuthManager;
import rowing.user.domain.user.AvailabilityIntervals;
import rowing.user.domain.user.AvailabilityNotFoundException;
import rowing.user.domain.user.User;
import rowing.user.domain.user.UserRepository;
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
    public DefaultController(AuthManager authManager, AvailabilityService availabilityService, UserRepository userRepository) {
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

    @PostMapping("/addUser")
    public ResponseEntity<User> addUser(@RequestBody User user){
        user.setUserId(authManager.getUsername());
        try {
            System.out.println(user);
            userRepository.save(user);
        } catch(Exception e){
            System.out.println(e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "INCORRECT DETAILS", e);
        }
        return ResponseEntity.ok(user);
    }
    @GetMapping("/{userId}/viewDetails")
    public ResponseEntity<User> getUserDetails(@PathVariable("userId") String userId){
        Optional<User> u = userRepository.findByUserId(userId);
        if(u.isPresent())
            return ResponseEntity.ok(u.get());
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "USER NOT FOUND");
    }
    /**
     * Add new availability if it doesn't exist or remove availability if it already exists for user with id userId.
     *
     * @return 200 OK if the userId and adding or removing is successful
     * @throws Exception if the userId doesn't exist
     */
    @PostMapping("/add-availability")
    public ResponseEntity addAvailability(@RequestPart("day") String day, @RequestPart("interval") String interval){
        String userId = authManager.getUsername();
        try {
            availabilityService.addAvailability(interval, day, userId);
        } catch (IllegalArgumentException | DateTimeException e){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "AVAILABILITY IS NOT IN THE CORRECT FORMAT", e);
        }
        return ResponseEntity.ok().build();
    }

    @PostMapping("/remove-availability")
    public ResponseEntity removeAvailability(@RequestPart("day") String day, @RequestPart("interval") String interval){
        String userId = authManager.getUsername();
        try {
            availabilityService.removeAvailability(interval, day, userId);
        } catch(AvailabilityNotFoundException e){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "AVAILABILITY NOT FOUND", e);
        } catch(IllegalArgumentException | DateTimeException e){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "AVAILABILITY IS NOT IN THE CORRECT FORMAT");
        }
        return ResponseEntity.ok().build();
    }

    @PostMapping("/edit-availability")
    public ResponseEntity editAvailability(@RequestPart("day") String day, @RequestPart("intervalOld") String intervalOld, @RequestPart("intervalNew") String intervalNew){
        String userId = authManager.getUsername();
        try{
            availabilityService.editAvailability(intervalOld, intervalNew, day, userId);
        } catch (AvailabilityNotFoundException e){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "AVAILABILITY NOT FOUND OR CANNOT BE REPLACED", e);
        } catch (IllegalArgumentException | DateTimeException e){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "AVAILABILITY IS NOT IN THE CORRECT FORMAT", e);
        }
        return ResponseEntity.ok().build();
    }
}
