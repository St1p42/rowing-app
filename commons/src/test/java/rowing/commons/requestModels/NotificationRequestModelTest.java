package rowing.commons.requestModels;

import org.junit.jupiter.api.Test;
import rowing.commons.NotificationStatus;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class NotificationRequestModelTest {
    @Test
    void constructorTest(){
        NotificationRequestModel req = new NotificationRequestModel(NotificationStatus.ACCEPTED, new UUID(101L, 2L));
        assertNotNull(req);
        assertEquals(NotificationStatus.ACCEPTED, req.getStatus());
        assertNotNull(req.getActivityId());
    }

}