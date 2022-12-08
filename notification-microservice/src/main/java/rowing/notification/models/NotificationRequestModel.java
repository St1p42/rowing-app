package rowing.notification.models;

import lombok.Data;
import rowing.commons.NotificationStatus;

@Data
public class NotificationRequestModel {

    private NotificationStatus status;
    private String email;
}
