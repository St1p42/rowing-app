package rowing.activity.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.deser.std.DateDeserializers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import rowing.activity.authentication.AuthManager;
import rowing.activity.domain.entities.Match;
import rowing.activity.domain.utils.Builder;
import rowing.activity.domain.CompetitionBuilder;
import rowing.activity.domain.Director;
import rowing.activity.domain.TrainingBuilder;
import rowing.activity.domain.entities.Activity;
import rowing.activity.domain.entities.Competition;
import rowing.activity.domain.entities.Training;
import rowing.activity.domain.repositories.ActivityRepository;
import rowing.activity.domain.repositories.MatchRepository;
import rowing.commons.AvailabilityIntervals;
import rowing.commons.NotificationStatus;
import rowing.commons.Position;
import rowing.commons.entities.ActivityDTO;
import rowing.commons.entities.CompetitionDTO;
import rowing.commons.entities.MatchingDTO;
import rowing.commons.entities.UserDTO;
import rowing.commons.entities.utils.JsonUtil;
import rowing.commons.models.NotificationRequestModel;
import rowing.commons.models.UserDTORequestModel;

import java.time.LocalTime;
import java.time.ZoneId;
import java.util.*;

@Service
public class ActivityService {
    private final transient ActivityRepository activityRepository;
    private final transient AuthManager authManager;
    private final transient MatchRepository matchRepository;
    @Autowired
    private RestTemplate restTemplate;

    @Value("${microserviceJWT}")
    String token;

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
        return "Hello " + authManager.getUsername();
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
        for(UUID id : uuids){
            if(matchRepository.existsByActivityId(id)){
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

    /**
     * Accepts the user to the activity, saves the match to the matching repo, and sends a notification to the user.
     *
     * @param activity that the owner wants to accept the user for
     * @param model the UserDTORequestModel keeping the information about the selected user and position
     * @return a String that notifies that the user is created successfully.
     * @throws JsonProcessingException if there is a problem occurs when converting the NotificationRequestModel object to Json
     */
    public String acceptUser(Activity activity, UserDTORequestModel model) throws JsonProcessingException {
        activity.getPositions().remove(model.getPositionSelected());
        Match<MatchingDTO> match = new Match<>(new MatchingDTO(UUID.randomUUID(), activity.getId(),
                model.getUserId(), model.getPositionSelected(), model.getGender(),
                model.getCompetitive(), model.getRowingOrganization(), model.getAvailability(), NotificationStatus.ACCEPTED));

        matchRepository.save(match);
        activityRepository.save(activity);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);

        NotificationRequestModel request = new NotificationRequestModel(model.getUserId(),
                NotificationStatus.ACCEPTED, activity.getId());

        String body = JsonUtil.serialize(request);
        HttpEntity requestEntity = new HttpEntity(body, headers);
        ResponseEntity responseEntity = restTemplate.exchange(
                "http://localhost:8082/notify",
                HttpMethod.POST, requestEntity, String.class);

        return "User " + model.getUserId() + " is accepted successfully";
    }


    /**
     * Method to update an activity in the repository.
     * Fields that can be updated are: name, start, location.
     *
     * @param activityId - the UUID corresponding to the activity that will be updated
     * @param updateActivityDto - the activityDto containing the information which the activity will be updated with
     * @return activityDto - the activityDto corresponding to the updated activity
     * @throws IllegalArgumentException - if the activity is not found in the database
     */
    public ActivityDTO updateActivity(UUID activityId, ActivityDTO updateActivityDto) throws IllegalArgumentException, JsonProcessingException {
        Optional<Activity> optionalActivity = activityRepository.findActivityById(activityId);
        if (optionalActivity.isPresent()) {
            throw new IllegalArgumentException("Activity does not exist !");
        }
        Activity activity = optionalActivity.get();

        NotificationRequestModel request = new NotificationRequestModel(activity.getOwner(),
                NotificationStatus.CHANGES,
                activity.getId());

        Optional<Date> optionalStart = Optional.ofNullable(updateActivityDto.getStart());
        if (optionalStart.isPresent()) {
            Date newStart = optionalStart.get();
            checkNewStart(newStart);  // Checking if the new start is in the future
            activity.setStart(newStart);
            request.setDate(newStart);
        }

        Optional.ofNullable(updateActivityDto.getName()).ifPresent(activity::setName);

        Optional<String> optionalLocation = Optional.ofNullable(updateActivityDto.getLocation());
        if (optionalLocation.isPresent()) {
            String newLocation = optionalLocation.get();
            activity.setLocation(newLocation);
            request.setLocation(newLocation);
        }

        //get all participants of the activity
        List<String> participants = new ArrayList<>();

        if(matchRepository.existsByActivityId(activityId)){
            List<Match> matches = matchRepository.findAllByActivityId(activityId);
            for(Match match : matches){
                if(match.getDto().getStatus() == NotificationStatus.ACCEPTED){
                    participants.add(match.getUserId());
                }
            }
        }

        // Send notification to all participants
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);

        String body = JsonUtil.serialize(request);
        HttpEntity requestEntity = new HttpEntity(body, headers);
        ResponseEntity responseEntity = restTemplate.exchange(
                "http://localhost:8082/notify",
                HttpMethod.POST, requestEntity, String.class);

        //Check if any participants are not available for the new date and remove them from the activity if they are not
        for (String userId : participants) {
            //building the request
            String uriUser = "http://localhost:8080/get-availability";
            HttpHeaders headersUser = new HttpHeaders();
            HttpEntity requestHttpUser = new HttpEntity(headers);

            //sending the request
            ResponseEntity<List<AvailabilityIntervals>> response = restTemplate.exchange(uriUser, HttpMethod.GET, requestHttpUser,
                    new ParameterizedTypeReference<List<AvailabilityIntervals>>() {
                    }, userId);

            //checking the response
            if (response.getStatusCode() == HttpStatus.OK) {
                List<AvailabilityIntervals> availability = response.getBody();
                if (!checkAvailability(activity, availability)) {
                    //remove the applicant
                }
            }

        }

        activityRepository.save(activity);

        ActivityDTO activityDto = activity.toDto();
        return activityDto;
    }

    /**
     * Method to check if the new start date is valid.
     *
     * @param newStart - the new start date
     * @throws IllegalArgumentException - if the new start date is invalid
     */
    private static void checkNewStart(Date newStart)
            throws IllegalArgumentException {
        Calendar calCurrent = Calendar.getInstance();  // Get local time
        calCurrent.setTime(Calendar.getInstance().getTime());
        var dayCurrent = calCurrent.get(Calendar.DAY_OF_WEEK);
        var timeCurrent = LocalTime.ofInstant(calCurrent.getTime().toInstant(), ZoneId.systemDefault());

        Calendar calNewStart = Calendar.getInstance();  // Get start time
        calNewStart.setTime(newStart);
        var dayNewStart = calNewStart.get(Calendar.DAY_OF_WEEK);
        var timeNewStart = LocalTime.ofInstant(calNewStart.getTime().toInstant(), ZoneId.systemDefault());

        if (dayCurrent == dayNewStart && timeCurrent.isAfter(timeNewStart)) {
            throw new IllegalArgumentException("Activity start time is in the past !");
        }
    }

}
