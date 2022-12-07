package rowing.notification.domain.notification;

public class NotificationReader {
    private transient NotificationStatus status;

    public NotificationReader(NotificationStatus status) {
        this.status = status;
    }

    /**
     * Converts notification to the string based on the status.
     *
     * @return The notification as a string message
     */
    public String read() {
        //TODO
        //implement messages based on the status
        return "";
    }
}
