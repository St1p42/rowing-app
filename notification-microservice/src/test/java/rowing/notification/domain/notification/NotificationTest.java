package rowing.notification.domain.notification;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;
import rowing.commons.NotificationStatus;
import rowing.commons.requestModels.NotificationRequestModel;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@SpringBootConfiguration
class NotificationTest {
    @Test
    void subjectAccepted() {
        NotificationRequestModel accepted = new NotificationRequestModel(NotificationStatus.ACCEPTED, new UUID(101L, 2L));
        Notification notificationAccepted = new Notification(accepted, "random@gmail.com");
        ReflectionTestUtils.setField(notificationAccepted, "subject", "Your status for the activity is ");
        String answer = "Your status for the activity is ACCEPTED";
        assertEquals(answer, notificationAccepted.retrieveSubject());
    }

    @Test
    void subjectRejected() {
        NotificationRequestModel rejected = new NotificationRequestModel(NotificationStatus.REJECTED, new UUID(101L, 2L));
        Notification notificationRejected = new Notification(rejected, "random@gmail.com");
        ReflectionTestUtils.setField(notificationRejected, "subject", "Your status for the activity is ");
        String answer = "Your status for the activity is REJECTED";
        assertEquals(answer, notificationRejected.retrieveSubject());
    }

    @Test
    void subjectKicked() {
        NotificationRequestModel kicked = new NotificationRequestModel(NotificationStatus.KICKED, new UUID(101L, 2L));
        Notification notificationKicked = new Notification(kicked, "random@gmail.com");
        ReflectionTestUtils.setField(notificationKicked, "subject", "Your status for the activity is ");
        String answer = "Your status for the activity is KICKED";
        assertEquals(answer, notificationKicked.retrieveSubject());
    }

    @Test
    void subjectDeleted() {
        NotificationRequestModel deleted = new NotificationRequestModel(NotificationStatus.DELETED, new UUID(101L, 2L));
        Notification notificationDeleted = new Notification(deleted, "random@gmail.com");
        ReflectionTestUtils.setField(notificationDeleted, "subject", "Your status for the activity is ");
        String answer = "Your status for the activity is DELETED";
        assertEquals(answer, notificationDeleted.retrieveSubject());
    }

    @Test
    void subjectWithdrawn() {
        NotificationRequestModel withdrawn = new NotificationRequestModel(NotificationStatus.WITHDRAWN, new UUID(101L, 2L));
        Notification notificationWithdrawn = new Notification(withdrawn, "random@gmail.com");
        ReflectionTestUtils.setField(notificationWithdrawn, "subject", "Your status for the activity is ");
        String answer = "Your status for the activity is WITHDRAWN";
        assertEquals(answer, notificationWithdrawn.retrieveSubject());
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
        NotificationRequestModel accepted = new NotificationRequestModel(NotificationStatus.ACCEPTED, new UUID(101L, 2L));
        Notification notificationAccepted = new Notification(accepted, "random@gmail.com");
        assertEquals("random@gmail.com", notificationAccepted.getDestinationEmail());
    }

    @Test
    void retrieveBodyAccepted() {
        String answer = "Congratulations, you were accepted to the activity with id: 00000000-0000-0065-0000-000000000002";
        NotificationRequestModel accepted = new NotificationRequestModel(NotificationStatus.ACCEPTED, new UUID(101L, 2L));
        Notification notificationAccepted = new Notification(accepted, "random@gmail.com");
        ReflectionTestUtils.setField(notificationAccepted, "acceptedBody",
                "Congratulations, you were accepted to the activity with id: ");
        assertEquals(answer, notificationAccepted.retrieveBody());
    }

    @Test
    void retrieveBodyRejected() {
        String answer = "Unfortunately, you were rejected to participate in this activity with id: "
                + "00000000-0000-0065-0000-000000000002";
        NotificationRequestModel rejected = new NotificationRequestModel(NotificationStatus.REJECTED, new UUID(101L, 2L));
        Notification notificationRejected = new Notification(rejected, "random@gmail.com");
        ReflectionTestUtils.setField(notificationRejected,
                "rejectedBody", "Unfortunately, you were rejected to participate in this activity with id: ");
        assertEquals(answer, notificationRejected.retrieveBody());
    }

    @Test
    void retrieveBodyWithdrawn() {
        NotificationRequestModel withdrawn = new NotificationRequestModel(NotificationStatus.WITHDRAWN, new UUID(101L, 2L));
        Notification notificationWithdrawn = new Notification(withdrawn, "random@gmail.com");
        ReflectionTestUtils.setField(notificationWithdrawn,
                "withdrawnBody", "You have successfully withdrawn your application from the activity with id: ");
        String answer = "You have successfully withdrawn your"
                + " application from the activity with id: 00000000-0000-0065-0000-000000000002";
        assertEquals(answer, notificationWithdrawn.retrieveBody());
    }

    @Test
    void retrieveBodyKicked() {
        NotificationRequestModel kicked = new NotificationRequestModel(NotificationStatus.KICKED, new UUID(101L, 2L));
        Notification notificationKicked = new Notification(kicked, "random@gmail.com");
        ReflectionTestUtils.setField(notificationKicked,
                "kickedBody", "Unfortunately, you were kicked from this activity with id: ");
        String answer = "Unfortunately, you were kicked from this activity with id: " + kicked.getActivityId();
        assertEquals(answer, notificationKicked.retrieveBody());
    }

    @Test
    void retrieveBodyDeleted() {
        NotificationRequestModel deleted = new NotificationRequestModel(NotificationStatus.DELETED,
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

}