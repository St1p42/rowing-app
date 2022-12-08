package rowing.notification.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.server.ResponseStatusException;
import rowing.notification.authentication.AuthManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import rowing.notification.domain.notification.Notification;
import rowing.notification.models.NotificationRequestModel;
import rowing.commons.NotificationStatus;

import rowing.notification.domain.notification.EmailService;

/**
 * Hello World example controller.
 * <p>
 * This controller shows how you can extract information from the JWT token.
 * </p>
 */
@RestController
public class DefaultController {
    @Autowired
    private transient EmailService senderService;

    private final transient AuthManager authManager;

    /**
     * Instantiates a new controller.
     *
     * @param authManager Spring Security component used to authenticate and authorize the user
     */
    @Autowired
    public DefaultController(AuthManager authManager) {
        this.authManager = authManager;
    }

    /**
     * Gets example by id.
     *
     * @return the example found in the database with the given id
     */
    @PostMapping("/notify")
    public ResponseEntity notifyUser(@RequestBody NotificationRequestModel request) {
        try{
            NotificationStatus status = request.getStatus();
            Notification notification = new Notification(status,
                    request.getEmail());
            senderService.sendEmail(notification);
        }catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.toString());
        }

        return ResponseEntity.ok().build();
    }
}
