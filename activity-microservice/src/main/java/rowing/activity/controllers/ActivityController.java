package rowing.activity.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import rowing.activity.authentication.AuthManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import rowing.activity.domain.entities.Activity;
import rowing.activity.domain.entities.Match;
import rowing.activity.domain.repositories.ActivityRepository;
import rowing.activity.domain.repositories.MatchRepository;
import rowing.activity.services.ActivityService;
import rowing.commons.Position;
import rowing.commons.entities.ActivityDTO;
import rowing.commons.entities.MatchingDTO;
import rowing.commons.entities.UserDTO;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Hello World example controller.
 * <p>
 * This controller shows how you can extract information from the JWT token.
 * </p>
 */
@RestController
@RequestMapping("/activity")
public class ActivityController {

    private final transient AuthManager authManager;
    private final transient ActivityRepository activityRepository;
    private final transient MatchRepository matchRepository;

    private final transient ActivityService activityService;

    /**
     * Instantiates a new controller.
     *
     * @param authManager     Spring Security component used to authenticate and authorize the user
     * @param matchRepository
     */
    @Autowired
    public ActivityController(AuthManager authManager, ActivityRepository activityRepository,
                              MatchRepository matchRepository, ActivityService activityService) {
        this.authManager = authManager;
        this.activityRepository = activityRepository;
        this.matchRepository = matchRepository;
        this.activityService = activityService;
    }

    /**
     * Gets example by id.
     *
     * @return the example found in the database with the given id
     */
    @GetMapping("/hello")
    public ResponseEntity<String> helloWorld() {
        return ResponseEntity.ok(activityService.hellWorld());

    }

    /**
     * Endpoint to create a new activity.
     *
     * @param dto that will contain basic activity information
     * @return response OK if the activity has been created
     */
    @PostMapping("/new")
    public ResponseEntity<String> createActivity(@RequestBody ActivityDTO dto) {
        return ResponseEntity.ok(activityService.createActivity(dto));
    }

    /**
     * Endpoint to retrieve every activity in the repository in a list of ActivityDTO objects.
     *
     * @return response OK if the activities are returned successfully
     */
    @GetMapping("/activityList")
    public ResponseEntity<List<ActivityDTO>> getActivities() {
        return ResponseEntity.ok(activityService.getActivities());
    }

    /**
     * Enpoint to remove activity based on the activity id.
     *
     * @param activityId - the id of the activity to be removed
     * @return activityDTO - the activity that has been deleted
     */
    @GetMapping("/{activityId}/delete")
    public ResponseEntity<ActivityDTO> deleteActivity(@PathVariable("activityId") UUID activityId) {
        ActivityDTO activityDTO;
        try {
            activityDTO = activityService.deleteActivity(activityId);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Activity was not found", e);
        }
        return ResponseEntity.ok(activityDTO);
    }

    /**
     * Endpoint that lets the user connect to the activity using the id.
     *
     * @param match dto containing information regarding the signup process
     * @return response if the sign-up was successful or not
     */
    @PostMapping("/sign/{activityId}")
    public ResponseEntity<String> signUpActivity(@RequestBody MatchingDTO match, @PathVariable UUID activityId) {
        String response = "";
        try {
            response = activityService.signUp(match);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
            //throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Activity was not found", e);
        }
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{activityId}/accept")
    public ResponseEntity<String> acceptUser(@PathVariable("activityId") UUID activityId,
                                             @RequestBody UserDTO user, @RequestBody Position position) throws JsonProcessingException {
        Optional<Activity> optionalActivity = activityRepository.findActivityById(activityId);
        if (!optionalActivity.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Activity does not exist !");
        }
        Activity activity = optionalActivity.get();
        if (!authManager.getUsername().equals(activity.getOwner())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Only the owner of the activity can accept users");
        }
        if (matchRepository.existsByActivityId(activityId)) {
            List<Match> matches = matchRepository.findAllByActivityId(activityId);
            for (Match match : matches) {
                if (match.getUserId().equals(user.getUserId())) {
                    return ResponseEntity.badRequest().body("This user is already participating in the activity");
                }
            }
        }
        if(!activity.getApplicants().contains(user.getUserId())){
            return ResponseEntity.badRequest().body("This user didn't apply to this activity");
        }
        if(!activity.getPositions().contains(position)){
            return ResponseEntity.badRequest().body("This position is already full");
        }

        return ResponseEntity.ok(activityService.acceptUser(activity, user, position));
    }
}