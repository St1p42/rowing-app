package rowing.activity.controllers;

import org.springframework.web.bind.annotation.*;
import rowing.activity.authentication.AuthManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import rowing.activity.domain.Builder;
import rowing.activity.domain.CompetitionBuilder;
import rowing.activity.domain.Director;
import rowing.activity.domain.TrainingBuilder;
import rowing.activity.domain.entities.Activity;
import rowing.activity.domain.entities.Competition;
import rowing.activity.domain.entities.Training;
import rowing.activity.domain.repositories.ActivityRepository;
import rowing.activity.services.ActivityService;
import rowing.commons.entities.ActivityDTO;
import rowing.commons.entities.CompetitionDTO;

import java.util.ArrayList;
import java.util.List;

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

    private final transient ActivityService activityService;

    /**
     * Instantiates a new controller.
     *
     * @param authManager Spring Security component used to authenticate and authorize the user
     */
    @Autowired
    public ActivityController(AuthManager authManager, ActivityRepository activityRepository,
                              ActivityService activityService) {
        this.authManager = authManager;
        this.activityRepository = activityRepository;
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
}