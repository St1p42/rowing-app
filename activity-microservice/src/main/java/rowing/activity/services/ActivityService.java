package rowing.activity.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rowing.activity.authentication.AuthManager;
import rowing.activity.domain.CompetitionBuilder;
import rowing.activity.domain.Director;
import rowing.activity.domain.TrainingBuilder;
import rowing.activity.domain.entities.Activity;
import rowing.activity.domain.entities.Competition;
import rowing.activity.domain.entities.Match;
import rowing.activity.domain.entities.Training;
import rowing.activity.domain.repositories.ActivityRepository;
import rowing.activity.domain.repositories.MatchRepository;
import rowing.activity.domain.utils.Builder;
import rowing.commons.AvailabilityIntervals;
import rowing.commons.entities.ActivityDTO;
import rowing.commons.entities.CompetitionDTO;

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
     * @param authManager that will be used
     * @param matchRepository that will be used to match users and activities
     */
    @Autowired
    public ActivityService(ActivityRepository activityRepository, AuthManager authManager,
                           MatchRepository matchRepository) {
        this.activityRepository = activityRepository;
        this.authManager = authManager;
        this.matchRepository = matchRepository;
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

        List<UUID> uuids = new ArrayList<>();
        for (Activity activity : activities) {

            if (activity.getStart().after(currentDate)) {
                activityDTOs.add(activity.toDto());
            } else {
                uuids.add(activity.getId());
                activityRepository.delete(activity);
            }
        }
        for (UUID id : uuids) {
            if (matchRepository.existsByActivityId(id)) {
                matchRepository.deleteAll(matchRepository.findAllByActivityId(id));
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
     * Signs off a user from an activity.
     *
     * @param activityId - activity to be signed off from
     * @return String - information about the user, activity and status of the operation.
     * @throws IllegalArgumentException - if the activity is not found, or the user is not signed up for this activity
     */
    public String signOff(UUID activityId) throws IllegalArgumentException {
        String username = authManager.getUsername();
        Optional<Activity> optionalActivity = activityRepository.findActivityById(activityId);
        if (!optionalActivity.isPresent()) {
            throw new IllegalArgumentException("Activity not found!");
        }
        Activity activity = optionalActivity.get();
        if (!activity.getApplicants().contains(username)) {
            throw new IllegalArgumentException("User has not signed-up for this activity");
        }
        Optional<Match> optionalMatch = matchRepository.findByActivityIdAndUserId(activityId, username);
        if (optionalMatch.isPresent()) {
            Match match = optionalMatch.get();
            activity.getPositions().add(match.getPosition());
            matchRepository.delete(match);
        }
        activity.getApplicants().remove(username);
        activityRepository.save(activity);
        return "User " + username + " has signed off from the activity : " + activityId;
    }


    /**
     * Method to check if the new start date is valid.
     *
     * @param newStart - the new start date
     * @throws IllegalArgumentException - if the new start date is invalid
     */
    public static boolean checkNewStart(Date newStart)
            throws IllegalArgumentException {
        Calendar calendar = Calendar.getInstance();
        Date currentDate = calendar.getTime();

        if (currentDate.after(newStart)) {
            throw new IllegalArgumentException("Activity start time is in the past !");
        }
        return true;
    }

}
