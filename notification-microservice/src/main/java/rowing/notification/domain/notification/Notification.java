package rowing.notification.domain.notification;

import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import rowing.commons.NotificationStatus;
import rowing.commons.models.NotificationRequestModel;

import java.util.HashMap;
import java.util.Map;

import static javax.swing.UIManager.put;


@Data
public class Notification {
    private String activityId;
    private NotificationStatus notificationStatus;
    private transient String destinationEmail;
    private String newLocation;
    private String newDate;
    private String username;
    private boolean useKafka = false;

    /**
     * Constructor for the notification object that is sent through email.
     *
     * @param requestInfo - info containing the status, email and activity id
     * @param email - the email of the user to be notified
     */
    public Notification(NotificationRequestModel requestInfo, String email) {
        if (isRequestInfoValid(requestInfo)) {
            this.notificationStatus = requestInfo.getStatus();
            this.activityId = getActivityId(requestInfo);
            this.newDate = getDate(requestInfo);
            this.newLocation = getLocation(requestInfo);
            this.destinationEmail = email;
        }
    }

    private boolean isRequestInfoValid(NotificationRequestModel requestInfo) {
        return requestInfo != null && requestInfo.getActivityId() != null;
    }

    private String getActivityId(NotificationRequestModel requestInfo) {
        return requestInfo.getActivityId().toString();
    }

    private String getDate(NotificationRequestModel requestInfo) {
        return requestInfo.getDate() != null ? requestInfo.getDate().toString() :
                "The date has not changed since the last update.";
    }

    private String getLocation(NotificationRequestModel requestInfo) {
        return requestInfo.getLocation() != null ? requestInfo.getLocation() :
                "The location has not changed since the last update.";
    }


    /**
     * Constructor for the notification object that is sent through kafka.
     *
     * @param requestInfo - info containing the status, email and activity id
     * @param username - the username of the user to be notified
     * @param useKafka - boolean that represents if kafka is to be used
     */
    public Notification(NotificationRequestModel requestInfo, String username, boolean useKafka) {
        if (isRequestInfoValid(requestInfo)) {
            this.notificationStatus = requestInfo.getStatus();
            this.activityId = getActivityId(requestInfo);
            this.newDate = getDate(requestInfo);
            this.newLocation = getLocation(requestInfo);
            this.username = username;
        }
        this.useKafka = useKafka;
    }

    /**
     * Getter for the destination email.
     *
     * @return destination email as a string
     */
    public String getDestinationEmail() {
        return this.destinationEmail;
    }
}
