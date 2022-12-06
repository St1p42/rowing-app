package nl.tudelft.sem.template.notification.domain.notification;

public class NotificationReader {
    private final NotificationStatus status;

    public NotificationReader(NotificationStatus status) {
        this.status = status;
    }

    public String read() {
        //TODO
        //implement messages based on the status
        return "";
    }
}
