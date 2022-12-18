package rowing.commons.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.Data;
import lombok.ToString;
import rowing.commons.NotificationStatus;
import rowing.commons.Position;
import rowing.commons.entities.utils.DTO;
import rowing.commons.entities.utils.Views;

import java.util.UUID;

/**
 * Dto for any activity.
 */
@Data
@ToString(callSuper = true)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
@JsonView(Views.Public.class)
public class MatchingDTO implements DTO {

    private UUID id;
    private UUID activityId;
    private UUID userId;
    private Position position;
    private NotificationStatus status;

    /**
     * Getter for the id.
     *
     * @return the id
     */
    public UUID getId() {
        return id;
    }

    /**
     * Constructor for Matching.
     *
     * @param id of the matching
     *
     * @param activityId of the activity
     *
     * @param userId of the user that matched/mismatched with the activity
     *
     * @param position of the user in the activity
     *
     * @param status of the user for participating in this activity
     */
    public MatchingDTO(UUID id, UUID activityId, UUID userId, Position position, NotificationStatus status) {
        this.id = id;
        this.activityId = activityId;
        this.userId = userId;
        this.position = position;
        this.status = status;
    }

    /**
     * A constructor for MatchingDTO.
     *
     * @param matchingDTO to be used
     */
    public MatchingDTO(MatchingDTO matchingDTO){
        this.id = matchingDTO.getId();
        this.position = matchingDTO.getPosition();
        this.activityId = matchingDTO.getActivityId();
        this.userId = matchingDTO.getUserId();
        this.status = matchingDTO.getStatus();
    }

    /**
     * Empty constructor for MatchingDTO.
     */
    public MatchingDTO() {
    }

}
