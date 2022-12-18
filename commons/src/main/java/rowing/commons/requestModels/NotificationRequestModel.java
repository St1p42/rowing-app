package rowing.commons.requestModels;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.Data;
import lombok.ToString;
import rowing.commons.NotificationStatus;
import rowing.commons.entities.utils.Views;

import java.util.UUID;

@Data
@ToString(callSuper = true)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
@JsonView(Views.Public.class)
public class NotificationRequestModel {

    private NotificationStatus status;

    private UUID activityId;

    /**
     * Constructor for notificationRequestModel
     * @param status the status of the notification
     * @param activityId the id of the activity
     */
    public NotificationRequestModel(NotificationStatus status, UUID activityId) {
        this.status = status;
        this.activityId = activityId;
    }

    public  NotificationRequestModel(){

    }
}
