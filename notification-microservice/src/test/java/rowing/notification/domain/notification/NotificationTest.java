package rowing.notification.domain.notification;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;
import rowing.commons.NotificationStatus;
import rowing.commons.models.NotificationRequestModel;

import java.util.Date;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@SpringBootConfiguration
class NotificationTest {

    @Test
    void constructorNoChanges() {
        NotificationRequestModel accepted = new NotificationRequestModel("alex",
                NotificationStatus.ACCEPTED, new UUID(101L, 2L));
        Notification notificationAccepted = new Notification(accepted, "random@gmail.com");
        assertEquals("The date has not changed since the last update.", notificationAccepted.getNewDate());
        assertEquals("The location has not changed since the last update.", notificationAccepted.getNewLocation());
        assertEquals("00000000-0000-0065-0000-000000000002", notificationAccepted.getActivityId());
        assertEquals(NotificationStatus.ACCEPTED, notificationAccepted.getNotificationStatus());
        assertEquals("random@gmail.com", notificationAccepted.getDestinationEmail());
    }

    @Test
    void constructorWithLocationChanges() {
        NotificationRequestModel accepted = new NotificationRequestModel("alex",
                NotificationStatus.CHANGES, new UUID(101L, 2L), "Delft");
        Notification notificationAccepted = new Notification(accepted, "random@gmail.com");
        assertEquals("The date has not changed since the last update.", notificationAccepted.getNewDate());
        assertEquals("Delft", notificationAccepted.getNewLocation());
        assertEquals("00000000-0000-0065-0000-000000000002", notificationAccepted.getActivityId());
        assertEquals(NotificationStatus.CHANGES, notificationAccepted.getNotificationStatus());
        assertEquals("random@gmail.com", notificationAccepted.getDestinationEmail());
    }

    @Test
    void constructorWithDateChanges() {
        Date date = new Date();
        NotificationRequestModel accepted = new NotificationRequestModel("alex",
                NotificationStatus.CHANGES, new UUID(101L, 2L), date);
        Notification notificationAccepted = new Notification(accepted, "random@gmail.com");
        assertEquals(date.toString(), notificationAccepted.getNewDate());
        assertEquals("The location has not changed since the last update.", notificationAccepted.getNewLocation());
        assertEquals("00000000-0000-0065-0000-000000000002", notificationAccepted.getActivityId());
        assertEquals(NotificationStatus.CHANGES, notificationAccepted.getNotificationStatus());
        assertEquals("random@gmail.com", notificationAccepted.getDestinationEmail());
    }

    @Test
    void constructorLocationChanged() {
        Date date = new Date();
        NotificationRequestModel changes = new NotificationRequestModel("alex",
                NotificationStatus.CHANGES, new UUID(101L, 2L), "Delft");
        Notification notificationChanges = new Notification(changes, "random@gmail.com");
        ReflectionTestUtils.setField(notificationChanges, "changesSubject",
                "The activity you have signed up for has some changes");
        ReflectionTestUtils.setField(notificationChanges, "changesBody",
                "There are some updates regarding the activity with id ");
        String subject = "The activity you have signed up for has some changes ";
        String body = notificationChanges.getChangesBody() + "00000000-0000-0065-0000-000000000002:\n"
                + "Date: " + "The date has not changed since the last update."
                + "\nLocation: Delft";
        assertEquals(subject, notificationChanges.retrieveSubject());
        assertEquals(body, notificationChanges.retrieveBody());
        assertEquals("The date has not changed since the last update.", notificationChanges.getNewDate());
        assertEquals("Delft", notificationChanges.getNewLocation());
    }

    @Test
    void subjectAccepted() {
        NotificationRequestModel accepted = new NotificationRequestModel("alex",
                NotificationStatus.ACCEPTED, new UUID(101L, 2L));
        Notification notificationAccepted = new Notification(accepted, "random@gmail.com");
        ReflectionTestUtils.setField(notificationAccepted, "subject", "Your status for the activity is ");
        String answer = "Your status for the activity is ACCEPTED";
        assertEquals(answer, notificationAccepted.retrieveSubject());
    }

    @Test
    void subjectRejected() {
        NotificationRequestModel rejected = new NotificationRequestModel("alex",
                NotificationStatus.REJECTED, new UUID(101L, 2L));
        Notification notificationRejected = new Notification(rejected, "random@gmail.com");
        ReflectionTestUtils.setField(notificationRejected, "subject", "Your status for the activity is ");
        String answer = "Your status for the activity is REJECTED";
        assertEquals(answer, notificationRejected.retrieveSubject());
    }

    @Test
    void subjectKicked() {
        NotificationRequestModel kicked = new NotificationRequestModel("alex",
                NotificationStatus.KICKED, new UUID(101L, 2L));
        Notification notificationKicked = new Notification(kicked, "random@gmail.com");
        ReflectionTestUtils.setField(notificationKicked, "subject", "Your status for the activity is ");
        String answer = "Your status for the activity is KICKED";
        assertEquals(answer, notificationKicked.retrieveSubject());
    }

    @Test
    void subjectDeleted() {
        NotificationRequestModel deleted = new NotificationRequestModel("alex",
                NotificationStatus.DELETED, new UUID(101L, 2L));
        Notification notificationDeleted = new Notification(deleted, "random@gmail.com");
        ReflectionTestUtils.setField(notificationDeleted, "subject", "Your status for the activity is ");
        String answer = "Your status for the activity is DELETED";
        assertEquals(answer, notificationDeleted.retrieveSubject());
    }

    @Test
    void subjectWithdrawn() {
        NotificationRequestModel withdrawn = new NotificationRequestModel("alex",
                NotificationStatus.WITHDRAWN, new UUID(101L, 2L));
        Notification notificationWithdrawn = new Notification(withdrawn, "random@gmail.com");
        ReflectionTestUtils.setField(notificationWithdrawn, "subject", "Your status for the activity is ");
        String answer = "Your status for the activity is WITHDRAWN";
        assertEquals(answer, notificationWithdrawn.retrieveSubject());
    }

    @Test
    void subjectChanges() {
        Date date = new Date();
        NotificationRequestModel changes = new NotificationRequestModel("alex",
                NotificationStatus.CHANGES, new UUID(101L, 2L), "Delft");
        Notification notificationChanges = new Notification(changes, "random@gmail.com");
        ReflectionTestUtils.setField(notificationChanges, "changesSubject",
                "The activity you have signed up for has some changes");
        String subject = "The activity you have signed up for has some changes ";
        assertEquals(subject, notificationChanges.retrieveSubject());
    }

    @Test
    void subjectNull() {
        Notification notificationNullStatus = new Notification(null, "random@gmail.com");
        ReflectionTestUtils.setField(notificationNullStatus, "subject", "Your status for the activity is ");
        String answer = "Your status for the activity is unknown";
        assertEquals(answer, notificationNullStatus.retrieveSubject());
    }


    @Test
    void getDestinationEmail() {
        NotificationRequestModel accepted = new NotificationRequestModel("alex",
                NotificationStatus.ACCEPTED, new UUID(101L, 2L));
        Notification notificationAccepted = new Notification(accepted, "random@gmail.com");
        assertEquals("random@gmail.com", notificationAccepted.getDestinationEmail());
    }

    @Test
    void retrieveBodyAccepted() {
        String answer = "Congratulations, you were accepted to the activity with id: 00000000-0000-0065-0000-000000000002";
        NotificationRequestModel accepted = new NotificationRequestModel("alex",
                NotificationStatus.ACCEPTED, new UUID(101L, 2L));
        Notification notificationAccepted = new Notification(accepted, "random@gmail.com");
        ReflectionTestUtils.setField(notificationAccepted, "acceptedBody",
                "Congratulations, you were accepted to the activity with id: ");
        assertEquals(answer, notificationAccepted.retrieveBody());
    }

    @Test
    void retrieveBodyRejected() {
        String answer = "Unfortunately, you were rejected to participate in this activity with id: "
                + "00000000-0000-0065-0000-000000000002";
        NotificationRequestModel rejected = new NotificationRequestModel("alex",
                NotificationStatus.REJECTED, new UUID(101L, 2L));
        Notification notificationRejected = new Notification(rejected, "random@gmail.com");
        ReflectionTestUtils.setField(notificationRejected,
                "rejectedBody", "Unfortunately, you were rejected to participate in this activity with id: ");
        assertEquals(answer, notificationRejected.retrieveBody());
    }

    @Test
    void retrieveBodyWithdrawn() {
        NotificationRequestModel withdrawn = new NotificationRequestModel("alex",
                NotificationStatus.WITHDRAWN, new UUID(101L, 2L));
        Notification notificationWithdrawn = new Notification(withdrawn, "random@gmail.com");
        ReflectionTestUtils.setField(notificationWithdrawn,
                "withdrawnBody", "You have successfully withdrawn your application from the activity with id: ");
        String answer = "You have successfully withdrawn your"
                + " application from the activity with id: 00000000-0000-0065-0000-000000000002";
        assertEquals(answer, notificationWithdrawn.retrieveBody());
    }

    @Test
    void retrieveBodyKicked() {
        NotificationRequestModel kicked = new NotificationRequestModel("alex",
                NotificationStatus.KICKED, new UUID(101L, 2L));
        Notification notificationKicked = new Notification(kicked, "random@gmail.com");
        ReflectionTestUtils.setField(notificationKicked,
                "kickedBody", "Unfortunately, you were kicked from this activity with id: ");
        String answer = "Unfortunately, you were kicked from this activity with id: " + kicked.getActivityId();
        assertEquals(answer, notificationKicked.retrieveBody());
    }

    @Test
    void retrieveBodyDeleted() {
        NotificationRequestModel deleted = new NotificationRequestModel("alex",
                NotificationStatus.DELETED,
                new UUID(101L, 2L));
        Notification notificationDeleted = new Notification(deleted, "random@gmail.com");
        ReflectionTestUtils.setField(notificationDeleted, "deletedBody",
                "Unfortunately, the owner deleted the activity with id: ");
        String answer = "Unfortunately, the owner deleted the activity with id: " + deleted.getActivityId();
        assertEquals(answer, notificationDeleted.retrieveBody());
    }
    
    @Test
    void retrieveBodyUnknownStatus() {
        Notification notificationNullStatus = new Notification(null, "random@gmail.com");
        ReflectionTestUtils.setField(notificationNullStatus,
                "defaultBody", "You have a notification regarding your activity with id: ");
        String answer = "You have a notification regarding your activity with id: Unknown";
        assertEquals(answer, notificationNullStatus.retrieveBody());
    }

    @Test
    void requestInfoNull() {
        Notification notificationNullStatus = new Notification(null, "random", true);
        assertEquals("Unknown", notificationNullStatus.getActivityId());
    }

    @Test
    void activityIdNull() {
        NotificationRequestModel accepted = new NotificationRequestModel("alex",
                NotificationStatus.ACCEPTED, null);
        Notification notificationAccepted = new Notification(accepted, "random@gmail.com");
        assertEquals("Unknown", notificationAccepted.getActivityId());

        notificationAccepted = new Notification(accepted, "random", true);
        assertEquals("Unknown", notificationAccepted.getActivityId());
    }
    @Test
    void retrieveBodyLocationChange() {
        NotificationRequestModel changes = new NotificationRequestModel("alex",
                NotificationStatus.CHANGES, new UUID(101L, 2L), "Delft");
        Notification notificationChanges = new Notification(changes, "random@gmail.com");
        ReflectionTestUtils.setField(notificationChanges, "changesSubject",
                "The activity you have signed up for has some changes");
        ReflectionTestUtils.setField(notificationChanges, "changesBody",
                "There are some updates regarding the activity with id ");
        String body = notificationChanges.getChangesBody() + "00000000-0000-0065-0000-000000000002:\n"
                + "Date: "
                + "The date has not changed since the last update."
                + "\nLocation: Delft";
        assertEquals(body, notificationChanges.retrieveBody());
    }

    @Test
    void retrieveBodyDateChange() {
        Date date = new Date();
        NotificationRequestModel changes = new NotificationRequestModel("alex",
                NotificationStatus.CHANGES, new UUID(101L, 2L), date);
        Notification notificationChanges = new Notification(changes, "random@gmail.com");
        ReflectionTestUtils.setField(notificationChanges, "changesBody",
                "There are some updates regarding the activity with id ");
        String subject = "The activity you have signed up for has some changes";
        String body = notificationChanges.getChangesBody() + "00000000-0000-0065-0000-000000000002:\n"
                + "Date: " + date + "\nLocation: The location has not changed since the last update.";
        assertEquals(body, notificationChanges.retrieveBody());
    }

}