package rowing.commons.requestModels;

import org.junit.jupiter.api.Test;
import rowing.commons.NotificationStatus;

import java.util.Date;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class NotificationRequestModelTest {
    @Test
    void constructorTest(){
        NotificationRequestModel req = new NotificationRequestModel("alex", NotificationStatus.ACCEPTED, new UUID(101L, 2L));
        assertNotNull(req);
        assertEquals(NotificationStatus.ACCEPTED, req.getStatus());
        assertNotNull(req.getActivityId());
        assertEquals("alex", req.getUsername());
    }

    @Test
    void constructor1Test(){
        NotificationRequestModel req = new NotificationRequestModel("alex", NotificationStatus.ACCEPTED, new UUID(101L, 2L), "Delft");
        assertNotNull(req);
        assertEquals(NotificationStatus.ACCEPTED, req.getStatus());
        assertNotNull(req.getActivityId());
        assertEquals("alex", req.getUsername());
        assertEquals("Delft", req.getLocation());
    }

    @Test
    void constructor2Test(){
        NotificationRequestModel req = new NotificationRequestModel("alex", NotificationStatus.ACCEPTED, new UUID(101L, 2L), new Date());
        assertNotNull(req);
        assertEquals(NotificationStatus.ACCEPTED, req.getStatus());
        assertNotNull(req.getActivityId());
        assertEquals("alex", req.getUsername());
        assertNotNull(req.getDate());
    }

}