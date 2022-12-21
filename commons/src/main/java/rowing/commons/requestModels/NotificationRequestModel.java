package rowing.commons.requestModels;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.Data;
import lombok.ToString;
import rowing.commons.NotificationStatus;
import rowing.commons.entities.utils.Views;

import java.util.Date;
import java.util.UUID;

@Data
@ToString(callSuper = true)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
@JsonView(Views.Public.class)
public class NotificationRequestModel {

    private String username;

    private String location;

    private Date date;

    private NotificationStatus status;

    private UUID activityId;

    /**
     * Constructor for notificationRequestModel for any status except date changed and location changed.
     *
     * @param status the status of the notification
     * @param activityId the id of the activity
     */
    public NotificationRequestModel(String username, NotificationStatus status, UUID activityId) {
        this.status = status;
        this.activityId = activityId;
        this.username = username;
    }

    /**
     * Constructor for notificationRequestModel for a location changed.
     *
     * @param status the status of the notification
     * @param activityId the id of the activity
     * @param location the new location of the activity
     */
    public NotificationRequestModel(String username, NotificationStatus status, UUID activityId, String location) {
        this.status = status;
        this.activityId = activityId;
        this.location = location;
        this.username = username;
    }

    /**
     * Constructor for notificationRequestModel for date changed.
     *
     * @param status the status of the notification
     * @param activityId the id of the activity
     * @param date the new date of the activity
     */
    public NotificationRequestModel(String username, NotificationStatus status, UUID activityId, Date date) {
        this.status = status;
        this.activityId = activityId;
        this.date = date;
        this.username = username;
    }

    public  NotificationRequestModel(){

    }
}
