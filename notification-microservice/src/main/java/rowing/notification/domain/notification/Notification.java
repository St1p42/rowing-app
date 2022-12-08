package rowing.notification.domain.notification;

import rowing.commons.NotificationStatus;

import java.util.Locale;

//write tests for this class
//later add the activity information to this class and change retrieveText respectively
public class Notification {
    private transient NotificationStatus status;
    private transient String destinationEmail;



    /**
     * Constructor for the notification object.
     *
     * @param status - status of the user regarding the activity he applied to
     * @param destinationEmail - an email of the user who should receive the notification
     */
    public Notification(NotificationStatus status, String destinationEmail) {
        this.status = status;
        this.destinationEmail = destinationEmail;
    }

    /**
     * Generate body text for the email based on the status.
     *
     * @return the body of an email in a string format
     */
    public String retrieveBody() {
        if (this.status == NotificationStatus.ACCEPTED) {
            return "You were " + this.status.toString().toLowerCase(Locale.ROOT)
                    + " to the activity!"; // + activity name, time, etc
        } else if (this.status == NotificationStatus.DELETED
                || this.status == NotificationStatus.REJECTED
                || this.status == NotificationStatus.KICKED) {
            return "Unfortunately, you were " + this.status.toString().toLowerCase(Locale.ROOT)
                    + " from the activity.";
        } else if (this.status == NotificationStatus.WITHDRAWN) {
            return "You have successfully " + this.status.toString().toLowerCase(Locale.ROOT)
                    + " from the activity";
        }

        //default case
        return "You have a notification regarding your activity";
    }

    /**
     * Retrieves the subject of an email based on the status of user in
     * this notification.
     *
     * @return String representing an email's subject
     */
    public String retrieveSubject() {
        return "Your status for the activity is " + this.status; //add info about activity
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
