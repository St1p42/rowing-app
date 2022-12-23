package rowing.notification.domain.notification;

import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Data
@Service
public class EmailService {
    @Autowired
    private transient JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    String emailAddress;

    /**
     * Sends a notification as an email to the given address.
     *
     * @param notification notification to send
     */
    public void sendEmail(Notification notification) {
        if (emailAddress == null
                || notification == null || notification.getDestinationEmail() == null
                || notification.retrieveBody() == null || notification.retrieveSubject() == null
                || !notification.getDestinationEmail().contains("@")) {
            throw new IllegalArgumentException();
        }
        SimpleMailMessage newMessage = new SimpleMailMessage();
        newMessage.setFrom(emailAddress);
        newMessage.setTo(notification.getDestinationEmail());
        newMessage.setText(notification.retrieveBody());
        newMessage.setSubject(notification.retrieveSubject());
        mailSender.send(newMessage);
        System.out.println("The email was sent successfully.");
    }
}
