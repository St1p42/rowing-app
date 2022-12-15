package rowing.activity.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rowing.activity.authentication.AuthManager;
import rowing.activity.domain.Builder;
import rowing.activity.domain.CompetitionBuilder;
import rowing.activity.domain.Director;
import rowing.activity.domain.TrainingBuilder;
import rowing.activity.domain.entities.Activity;
import rowing.activity.domain.entities.Competition;
import rowing.activity.domain.entities.Training;
import rowing.activity.domain.repositories.ActivityRepository;
import rowing.commons.entities.ActivityDTO;
import rowing.commons.entities.CompetitionDTO;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ActivityService {
    private final transient ActivityRepository activityRepository;
    private final transient AuthManager authManager;

    @Autowired
    public ActivityService(ActivityRepository activityRepository, AuthManager authManager) {
        this.activityRepository = activityRepository;
        this.authManager = authManager;
    }

    /**
     * Gets example by id.
     *
     * @return the example found in the database with the given id
     */
    public String hellWorld() {
        return "Hello " + authManager.getNetId();
    }

    /**
     * Method to create a new activity and add it to the repository.
     *
     * @param dto that will contain basic activity information
     *
     * @return the string will be returned if the activity is added successfully
     */
    public String createActivity(ActivityDTO dto) {
        Builder builder;
        Director director;
        System.out.print("\n\n\n\n\n\nDTO TYPE :" + dto.getType() + "\n\n\n\n\n\n");
        if (dto.getType().equals("Training")) {
            builder = new TrainingBuilder();
            director = new Director();
            director.constructTraining((TrainingBuilder) builder, dto);
            Training activity = (Training) builder.build();
            activityRepository.save(activity);
            return "Activity " + activity.getId() + " was created successfully !";
        } else {
            builder = new CompetitionBuilder();
            director = new Director();
            director.constructCompetition((CompetitionBuilder) builder, (CompetitionDTO) dto);
            Competition activity = (Competition) builder.build();
            activityRepository.save(activity);
            return "Activity " + activity.getId() + "created successfully !";
        }
    }

    /**
     * Method to retrieve every activity in the repository in a list of ActivityDTO objects.
     *
     * @return list of all activities stored in the database
     */
    public List<ActivityDTO> getActivities() {
        List<Activity> activities = activityRepository.findAll();
        List<ActivityDTO> activityDtos = new ArrayList<>();
        for (Activity activity : activities) {
            activityDtos.add(activity.toDto());
        }
        return activityDtos;
    }

    /**
     * Deletes the activity with the specified id from the database.
     *
     * @param activityId - the UUID corresponding to the activity
     * @return activityDto - the activityDto corresponding to the deleted activity
     * @throws IllegalArgumentException - if the activity is not found in the database
     */
    public ActivityDTO deleteActivity(UUID activityId) throws IllegalArgumentException {
        Optional<Activity> activity = activityRepository.findActivityById(activityId);
        if (activity.isPresent()) {
            ActivityDTO activityDto = activity.get().toDto();
            activityRepository.delete(activity.get());
            return activityDto;
        }
        throw new IllegalArgumentException();
    }
}
