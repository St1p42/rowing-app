package rowing.notification;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import rowing.notification.domain.notification.EmailService;
import rowing.notification.domain.notification.Notification;

/**
 * Example microservice application.
 */
@SpringBootApplication
public class Application {
    @Autowired
    private EmailService senderService;
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @EventListener(ApplicationReadyEvent.class)
    public void sendMail() {
        //checking whether works
        Notification.NotificationStatus status = Notification.NotificationStatus.ACCEPTED;
        Notification notification = new Notification(status,
                "halitgulamov@gmail.com");
        senderService.sendEmail(notification);
    }

}
