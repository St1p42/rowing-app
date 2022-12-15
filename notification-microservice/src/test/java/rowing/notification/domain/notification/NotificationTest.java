package rowing.notification.domain.notification;

import org.junit.jupiter.api.Test;
import rowing.commons.NotificationStatus;

import static org.junit.jupiter.api.Assertions.*;

class NotificationTest {
    Notification notificationAccepted = new Notification(NotificationStatus.ACCEPTED,
            "random@gmail.com");
    Notification notificationRemoved = new Notification(NotificationStatus.REJECTED,
            "random@gmail.com");
    Notification notificationWithdrawn = new Notification(NotificationStatus.WITHDRAWN,
            "random@gmail.com");
    Notification notificationNullStatus = new Notification(null, "random@gmail.com");

    @Test
    void subjectAccepted() {
        String answer = "Your status for the activity is ACCEPTED";
        assertEquals(answer, notificationAccepted.retrieveSubject());
    }

    @Test
    void subjectRemovedOrRejected() {
        String answer = "Your status for the activity is REJECTED";
        assertEquals(answer, notificationRemoved.retrieveSubject());
    }

    @Test
    void subjectWithdrawn() {
        String answer = "Your status for the activity is WITHDRAWN";
        assertEquals(answer, notificationWithdrawn.retrieveSubject());
    }

    @Test
    void subjectNull() {
        String answer = "Your status for the activity is unknown";
        assertEquals(answer, notificationNullStatus.retrieveSubject());
    }


    @Test
    void getDestinationEmail() {
        assertEquals("random@gmail.com", notificationAccepted.getDestinationEmail());
    }

    @Test
    void retrieveBodyAccepted() {
        String answer = "You were accepted to the activity!";
        assertEquals(answer, notificationAccepted.retrieveBody());
    }

    @Test
    void retrieveBodyRejectedOrRemoved() {
        String answer = "Unfortunately, you were rejected from the activity.";
        assertEquals(answer, notificationRemoved.retrieveBody());
    }

    @Test
    void retrieveBodyWithdrawn() {
        String answer = "You have successfully withdrawn from the activity.";
        assertEquals(answer, notificationWithdrawn.retrieveBody());
    }

    @Test
    void retrieveBodyUnknownStatus() {
        String answer = "You have a notification regarding your activity";
        assertEquals(answer, notificationNullStatus.retrieveBody());
    }

}