package rowing.notification.domain.notification;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import rowing.commons.NotificationStatus;
import rowing.commons.requestModels.NotificationRequestModel;

import java.util.Date;

//write tests for this class
//later add the activity information to this class and change retrieveText respectively
@Data
public class Notification {
    private String activityId;
    private NotificationStatus notificationStatus;
    private transient String destinationEmail;
    private String newLocation;
    private String newDate;

    @Value("${body.notification.accepted}")
    private String acceptedBody;

    @Value("${body.notification.rejected}")
    private String rejectedBody;

    @Value("${body.notification.deleted}")
    private String deletedBody;

    @Value("${body.notification.kicked}")
    private String kickedBody;

    @Value("${body.notification.withdrawn}")
    private String withdrawnBody;

    @Value("${body.notification.default}")
    private String defaultBody;

    @Value("${subject.notification.general}")
    private String subject;

    @Value("${subject.notification.activityChanges}")
    private String changesSubject;

    @Value("${body.notification.activityChanges}")
    private String changesBody;

    private String username;
    private boolean useKafka = false;

    String unknown = "Unknown";


    /**
     * Constructor for the notification object that is sent through email.
     *
     * @param requestInfo - info containing the status, email and activity id
     * @param email - the email of the user to be notified
     */
    public Notification(NotificationRequestModel requestInfo, String email) {
        if (requestInfo == null || requestInfo.getActivityId() == null) {
            this.activityId = "Unknown";
        } else {
            this.notificationStatus = requestInfo.getStatus();
            this.activityId = requestInfo.getActivityId().toString();
            if (requestInfo.getDate() != null) {
                this.newDate = requestInfo.getDate().toString();
            } else {
                this.newDate = "The date has not changed since the last update.";
            }

            if (requestInfo.getLocation() != null) {
                this.newLocation = requestInfo.getLocation();
            } else {
                this.newLocation = "The location has not changed since the last update.";
            }

            this.destinationEmail = email;
        }
    }

    /**
     * Constructor for the notification object that is sent through kafka.
     *
     * @param requestInfo - info containing the status, email and activity id
     * @param username - the username of the user to be notified
     * @param useKafka - boolean that represents if kafka is to be used
     */
    public Notification(NotificationRequestModel requestInfo, String username, boolean useKafka) {
        if (requestInfo == null || requestInfo.getActivityId() == null) {
            this.activityId = "Unknown";
        } else {
            this.notificationStatus = requestInfo.getStatus();
            this.activityId = requestInfo.getActivityId().toString();
            if (requestInfo.getDate() != null) {
                this.newDate = requestInfo.getDate().toString();
            } else {
                this.newDate = "The date has not changed since the last update.";
            }

            if (requestInfo.getLocation() != null) {
                this.newLocation = requestInfo.getLocation();
            } else {
                this.newDate = "The location has not changed since the last update.";
            }

            this.username = username;
        }
        this.useKafka = useKafka;
    }

    /**
     * Generate body text for the email based on the status.
     *
     * @return the body of an email in a string format
     */
    public String retrieveBody() {
        if (notificationStatus == null) {
            return defaultBody + activityId;
        }
        switch (notificationStatus) {
            case ACCEPTED:
                return acceptedBody + activityId;
            case DELETED:
                return deletedBody + activityId;
            case REJECTED:
                return rejectedBody + activityId;
            case KICKED:
                return kickedBody + activityId;
            case WITHDRAWN:
                return withdrawnBody + activityId;
            case CHANGES:
                return changesBody + activityId + ":\n"
                        + "Date: " + newDate + "\nLocation: " + newLocation;
            default:
                return defaultBody + activityId;
        }
    }

    /**
     * Retrieves the subject of an email based on the status of user in
     * this notification.
     *
     * @return String representing an email's subject
     */
    public String retrieveSubject() {
        if (notificationStatus == null) {
            return subject + "unknown";
        }
        if (notificationStatus == NotificationStatus.CHANGES) {
            return changesSubject;
        }
        return subject + notificationStatus; //add info about activity
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
