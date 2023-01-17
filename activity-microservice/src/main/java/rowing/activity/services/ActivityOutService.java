package rowing.activity.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import rowing.activity.domain.entities.Activity;
import rowing.activity.domain.entities.Competition;
import rowing.activity.domain.entities.Match;
import rowing.activity.domain.repositories.ActivityRepository;
import rowing.activity.domain.repositories.MatchRepository;
import rowing.commons.AvailabilityIntervals;
import rowing.commons.NotificationStatus;
import rowing.commons.entities.ActivityDTO;
import rowing.commons.entities.MatchingDTO;
import rowing.commons.entities.UserDTO;
import rowing.commons.entities.utils.JsonUtil;
import rowing.commons.models.NotificationRequestModel;
import rowing.commons.models.UserDTORequestModel;

import java.util.*;

import static rowing.activity.services.ActivityService.checkAvailability;
import static rowing.activity.services.ActivityService.checkNewStart;

@Service
public class ActivityOutService {

    private final transient ActivityRepository activityRepository;
    private final transient MatchRepository matchRepository;

    @Autowired
    private transient RestTemplate restTemplate;

    /**
     * Constructor for the ActivityOutService class.
     *
     * @param activityRepository that will be used to keep info about activities
     * @param matchRepository that will be used to match users and activities
     */
    @Autowired
    public ActivityOutService(ActivityRepository activityRepository,
                           MatchRepository matchRepository) {
        this.activityRepository = activityRepository;
        this.matchRepository = matchRepository;
    }



    /**
     * Method to return all participants of an activity.
     *
     * @param activityId of the activity
     * @return list of users
     */
    public List<UserDTO> getParticipants(UUID activityId) {
        List<String> ids = new ArrayList<>();

        if (matchRepository.existsByActivityId(activityId)) {
            List<Match> matches = matchRepository.findAllByActivityId(activityId);
            for (Match match : matches) {
                if (match.getDto().getStatus() == NotificationStatus.ACCEPTED) {
                    ids.add(match.getUserId());
                }
            }
        }

        List<UserDTO> users = new ArrayList<>();

        for (String id : ids) {
            users.add(getUser(id));
        }

        return users;
    }

    /**
     * Returns the userDTO object for the corresponding username.
     *
     * @param userId of the user
     * @return the userDTO object of the user
     */
    public UserDTO getUser(String userId) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(ServiceConfig.token);

        HttpEntity<String> requestEntity = new HttpEntity<>(userId, headers);
        ResponseEntity<UserDTO> response = restTemplate
                .exchange(ServiceConfig.urlNotification + ":" + ServiceConfig.portUsers + "/user/" + userId + "/get-user",
                        HttpMethod.GET, requestEntity, UserDTO.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            return response.getBody();
        }
        throw new IllegalArgumentException();
    }

    /**
     * Returns the activity with the specified id from the database.
     *
     * @param match dto that contains information about the singUp / match process
     * @return String - the response corresponding to the signUp result
     * @throws IllegalArgumentException - if the activity is not found in the database or user is not compatible
     */
    public String signUp(MatchingDTO match) throws IllegalArgumentException, JsonProcessingException {

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
            activityPresent = activityRepository.save(activityPresent);

            if (activityPresent.getPositions().size() < 1) {

                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                headers.setBearerAuth(ServiceConfig.token);

                NotificationRequestModel request = new NotificationRequestModel(match.getUserId(),
                        NotificationStatus.ACTIVITY_FULL, activityPresent.getId());

                String body = JsonUtil.serialize(request);
                HttpEntity requestEntity = new HttpEntity(body, headers);
                ResponseEntity responseEntity = restTemplate.exchange(
                        ServiceConfig.urlNotification + ":" + ServiceConfig.portNotification + ServiceConfig.pathNotify,
                        HttpMethod.POST, requestEntity, String.class);
                return "User " + match.getUserId() + " signed up for activity : " + match.getActivityId().toString()
                        + " but since activity was full the user is currently in the waitlist.";
            }

            return "User " + match.getUserId() + " signed up for activity : " + match.getActivityId().toString();
        }
        throw new IllegalArgumentException("Activity does not exist !");
    }


    /**
     * Gets the list of accepted participants of an activity.
     *
     * @param activityId - the UUID corresponding to the activity
     * @return the list of accepted participants
     */
    public List<String> getParticipantIDs(UUID activityId) {
        List<String> participants = new ArrayList<>();

        if (matchRepository.existsByActivityId(activityId)) {
            List<Match> matches = matchRepository.findAllByActivityId(activityId);
            for (Match match : matches) {
                if (match.getDto().getStatus() == NotificationStatus.ACCEPTED) {
                    participants.add(match.getUserId());
                }
            }
        }
        return participants;
    }

    /**
     * Accepts the user to the activity, saves the match to the matching repo, and sends a notification to the user.
     *
     * @param activity that the owner wants to accept the user for
     * @param model the UserDTORequestModel keeping the information about the selected user and position
     * @return a String that notifies that the user is accepted successfully.
     * @throws JsonProcessingException if there is a problem occurs when converting
     *         the NotificationRequestModel object to Json
     */
    public String acceptUser(Activity activity, UserDTORequestModel model) throws JsonProcessingException {
        activity.getPositions().remove(model.getPositionSelected());
        Match<MatchingDTO> match = new Match<>(new MatchingDTO(UUID.randomUUID(), activity.getId(),
                model.getUserId(), model.getPositionSelected(), model.getGender(),
                model.getCompetitive(), model.getRowingOrganization(),
                model.getAvailability(), NotificationStatus.ACCEPTED));

        match = matchRepository.save(match);
        activity = activityRepository.save(activity);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(ServiceConfig.token);

        NotificationRequestModel request = new NotificationRequestModel(model.getUserId(),
                NotificationStatus.ACCEPTED, activity.getId());

        String body = JsonUtil.serialize(request);
        HttpEntity requestEntity = new HttpEntity(body, headers);
        ResponseEntity responseEntity = restTemplate.exchange(
                ServiceConfig.urlNotification + ":" + ServiceConfig.portNotification + ServiceConfig.pathNotify,
                HttpMethod.POST, requestEntity, String.class);
        String response = "User "  + model.getUserId() + " is accepted successfully to the activity with id "
                + activity.getId();
        if (activity.getPositions().size() < 1) {
            List<String> applicants = activity.getApplicants();
            for (String user : applicants) {
                if (!matchRepository.existsByActivityIdAndUserId(match.getActivityId(), user)) {
                    request = new NotificationRequestModel(user,
                            NotificationStatus.ACTIVITY_FULL, activity.getId());
                    body = JsonUtil.serialize(request);
                    requestEntity = new HttpEntity(body, headers);
                    responseEntity = restTemplate.exchange(
                            ServiceConfig.urlNotification + ":" + ServiceConfig.portNotification + ServiceConfig.pathNotify,
                            HttpMethod.POST, requestEntity, String.class);
                    response += "\nUser " + user + " is currently in the waitlist since the activity was full.";
                }
            }
        }
        return response;

    }

    /**
     * Rejects the user applied to the activity, and sends a notification to the user.
     *
     * @param activity that the owner wants to reject the user for
     * @param model the UserDTO keeping the information about the selected user
     * @return a String that notifies that the user is rejected successfully.
     * @throws JsonProcessingException if there is a problem occurs when converting
     *         the NotificationRequestModel object to Json
     */
    public String rejectUser(Activity activity, UserDTO model) throws JsonProcessingException {
        activity.getApplicants().remove(model.getUserId());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(ServiceConfig.token);

        NotificationRequestModel request = new NotificationRequestModel(model.getUserId(),
                NotificationStatus.REJECTED, activity.getId());

        String body = JsonUtil.serialize(request);
        HttpEntity requestEntity = new HttpEntity(body, headers);
        ResponseEntity responseEntity = restTemplate.exchange(
                ServiceConfig.urlNotification + ":" + ServiceConfig.portNotification + ServiceConfig.pathNotify,
                HttpMethod.POST, requestEntity, String.class);

        return "User " + model.getUserId() + " is rejected successfully";
    }


    /**
     * Function that kicks an user from the repository.
     *
     * @param activity activity information
     * @param userId id of the user that needs to be kicked
     * @return message containing if the user has been kicked or not
     * @throws IllegalArgumentException if the input is invalid
     */
    public String kickUser(Activity activity, String userId) throws IllegalArgumentException {
        boolean signedUp = false;
        System.out.println(userId + "\n");
        for (int i = 0; i < activity.getApplicants().size(); i++) {
            System.out.println(activity.getApplicants().get(i).toString());
            if (activity.getApplicants().get(i).equals(userId)) {
                List<String> list = activity.getApplicants();
                list.remove(i);
                activity.setApplicants(list);
                signedUp = true;
            }
        }
        if (!signedUp) {
            throw new IllegalArgumentException("User " + userId + " was not signed up for this activity !");
        }
        activityRepository.save(activity);
        Optional<Match> match = matchRepository.findByActivityIdAndUserId(activity.getId(), userId);
        if (match.isPresent()) {
            matchRepository.delete(match.get());
            return "User " + userId + " is no longer participating !";
        }
        return "User " + userId + " kicked successfully !";
    }

    /**
     * Method to update an activity in the repository.
     * Fields that can be updated are: start time, location.
     *
     * @param activityId - the UUID corresponding to the activity that will be updated
     * @param updateActivityDto - the activityDto containing the information which the activity will be updated with
     * @return activityDto - the activityDto corresponding to the updated activity
     * @throws IllegalArgumentException - if the activity is not found in the database
     * @throws JsonProcessingException - if there is a problem occurs when converting
     */
    public String updateActivity(UUID activityId, ActivityDTO updateActivityDto)
            throws IllegalArgumentException, JsonProcessingException {
        Optional<Activity> optionalActivity = activityRepository.findActivityById(activityId);
        if (optionalActivity.isEmpty()) {
            throw new IllegalArgumentException("Activity does not exist !");
        }
        Activity activity = optionalActivity.get();

        NotificationRequestModel requestModel = new NotificationRequestModel(null,
                NotificationStatus.CHANGES,
                activity.getId());

        Optional<Date> optionalStart = Optional.ofNullable(updateActivityDto.getStart());
        if (optionalStart.isPresent()) {
            Date newStart = optionalStart.get();
            checkNewStart(newStart);  // Checking if the new start is in the future
            activity.setStart(newStart);
            requestModel.setDate(newStart);
        }

        Optional<String> optionalLocation = Optional.ofNullable(updateActivityDto.getLocation());
        if (optionalLocation.isPresent()) {
            String newLocation = optionalLocation.get();
            activity.setLocation(newLocation);
            requestModel.setLocation(newLocation);
        }

        //get all participants of the activity
        List<String> participants = getParticipantIDs(activityId);

        // Send notification to all participants
        String uriNotification = ServiceConfig.urlNotification + ":"
                + ServiceConfig.portNotification + ServiceConfig.pathNotify;
        HttpHeaders headersNotification = new HttpHeaders();
        headersNotification.setContentType(MediaType.APPLICATION_JSON);
        headersNotification.setBearerAuth(ServiceConfig.token);

        for (String username : participants) {
            requestModel.setUsername(username);
            String bodyNotification = JsonUtil.serialize(requestModel);
            HttpEntity requestNotification = new HttpEntity(bodyNotification, headersNotification);
            ResponseEntity<String> responseNotification = restTemplate.exchange(
                    uriNotification,
                    HttpMethod.POST, requestNotification, String.class);


            //Check if any participants are not available for the new date and remove them from the activity if they are not
            if (optionalStart.isPresent()) {
                //building the request for the user availability
                String uriUser = ServiceConfig.urlNotification + ":"
                        + ServiceConfig.portUsers + ServiceConfig.pathUserController + ServiceConfig.pathUserAvailability;
                HttpHeaders headersUser = new HttpHeaders();
                headersUser.setContentType(MediaType.APPLICATION_JSON);
                headersUser.setBearerAuth(ServiceConfig.token);
                HttpEntity requestHttpUser = new HttpEntity(username, headersUser);

                //sending the request
                ResponseEntity<List<AvailabilityIntervals>> response =
                        restTemplate.exchange(uriUser, HttpMethod.GET, requestHttpUser,
                                new ParameterizedTypeReference<List<AvailabilityIntervals>>() {
                                }, username);

                //checking the response
                if (response.getStatusCode() == HttpStatus.OK) {
                    List<AvailabilityIntervals> availability = response.getBody();
                    if (!checkAvailability(activity, availability)) {
                        kickUser(activity, username);
                    }
                }
            }
        }

        activityRepository.save(activity);

        ActivityDTO activityDto = activity.toDto();
        return "Activity" + activityId + "has been updated successfully";
    }

}
