package rowing.activity.services;

import com.fasterxml.jackson.databind.deser.std.DateDeserializers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rowing.activity.authentication.AuthManager;
import rowing.activity.domain.utils.Builder;
import rowing.activity.domain.CompetitionBuilder;
import rowing.activity.domain.Director;
import rowing.activity.domain.TrainingBuilder;
import rowing.activity.domain.entities.Activity;
import rowing.activity.domain.entities.Competition;
import rowing.activity.domain.entities.Match;
import rowing.activity.domain.entities.Training;
import rowing.activity.domain.repositories.ActivityRepository;
import rowing.activity.domain.repositories.MatchRepository;
import rowing.commons.AvailabilityIntervals;
import rowing.commons.entities.ActivityDTO;
import rowing.commons.entities.CompetitionDTO;
import rowing.commons.entities.MatchingDTO;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.*;

@Service
public class ActivityService {
    private final transient ActivityRepository activityRepository;
    private final transient AuthManager authManager;
    private final transient MatchRepository matchRepository;

    /**
     * Constructor for the ActivityService class.
     *
     * @param activityRepository that will be used to keep info about activities
     *
     * @param authManager that will be used
     *
     * @param matchRepository that will be used to match users and activities
     */
    @Autowired
    public ActivityService(ActivityRepository activityRepository, AuthManager authManager, MatchRepository matchRepository) {
        this.activityRepository = activityRepository;
        this.authManager = authManager;
        this.matchRepository = matchRepository;
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
        if (dto.getType().equals("Training")) {
            builder = new TrainingBuilder();
            director = new Director();
            director.constructTrainingDTO((TrainingBuilder) builder, dto);
            Training activity = (Training) builder.build();
            activityRepository.save(activity);
            return "Activity " + activity.getId() + " was created successfully !";
        } else {
            builder = new CompetitionBuilder();
            director = new Director();
            director.constructCompetitionDTO((CompetitionBuilder) builder, (CompetitionDTO) dto);
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
        Calendar calendar = Calendar.getInstance();
        Date currentDate = calendar.getTime();
        List<Activity> activities = activityRepository.findAll();
        List<ActivityDTO> activityDTOs = new ArrayList<>();
        for (Activity activity : activities) {

            if (activity.getStart().after(currentDate)) {
                activityDTOs.add(activity.toDto());
            } else {
                activityRepository.delete(activity);
            }
        }
        return activityDTOs;
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

    /**
     * Function that checks wether a schedule is available for an activity.
     *
     * @param activity that needs to fit in the availability
     * @param availability list of intervals that could fit our activity start time
     * @return true or false
     */
    public static boolean checkAvailability(Activity activity, List<AvailabilityIntervals> availability) {
        Calendar cal = Calendar.getInstance();  // Checking availability
        cal.setTime(activity.getStart());
        var day = cal.get(Calendar.DAY_OF_WEEK);
        var time = LocalTime.ofInstant(cal.getTime().toInstant(), ZoneId.systemDefault());
        boolean available = false;

        for (AvailabilityIntervals interval : availability) {
            if (interval.getDay().getValue() == day
                    && interval.getStartInterval().isBefore(time) && interval.getEndInterval().isAfter(time)) {
                available = true;
            }
        }
        return available;
    }

    /**
     * Returns the activity with the specified id from the database.
     *
     * @param match dto that contains information about the singUp / match process
     * @return String - the response corresponding to the signUp result
     * @throws IllegalArgumentException - if the activity is not found in the database or user is not compatible
     */
    public String signUp(MatchingDTO match) throws IllegalArgumentException {

        Optional<Activity> activity = activityRepository.findActivityById(match.getActivityId());
        if (activity.isPresent()) {
            Activity activityPresent = activity.get(); // Checking if a user is already signed up for this
            List<String> signUps = activityPresent.getApplicants();
            for (String userId : signUps) {
                if (userId.equals(match.getUserId())) {
                    throw new IllegalArgumentException("User already signed up for this activity !\n");
                }
            }

            if (!checkAvailability(activityPresent, match.getAvailability())) {
                throw new IllegalArgumentException("User is not available for this activity !");
            }
            if (activityPresent instanceof Competition) {   // Checking competition requirements
                Competition competition = (Competition) activityPresent; 
                if (!match.getCompetitive()) {
                    throw new IllegalArgumentException("User is not competitive!");
                }
                if (competition.getGender() != null
                        && (competition.getGender() != match.getGender())) {
                    throw new IllegalArgumentException("User does not fit gender requirements !");
                }
                if (competition.getOrganisation() != null
                        && (!competition.getOrganisation().equals(match.getOrganisation()))) {
                    throw new IllegalArgumentException("User is not part of the organisation !");
                }
            }

            activityPresent.addApplicant(match.getUserId()); // If all is fine we add the applicant
            activityRepository.save(activityPresent);

            return "User " + match.getUserId() + " signed up for activity : " + match.getActivityId().toString();
        }
        throw new IllegalArgumentException("Activity does not exist !");
    }

}
