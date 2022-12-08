package rowing.notification.domain.notification;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;


    public void sendEmail(Notification notification) {
        SimpleMailMessage newMessage = new SimpleMailMessage();
        newMessage.setFrom("sem33b.notification@gmail.com");
        newMessage.setTo(notification.getDestinationEmail());
        newMessage.setText(notification.retrieveBody());
        newMessage.setSubject(notification.retrieveSubject());
        mailSender.send(newMessage);
        System.out.println("The email was sent successfully.");

    }
}
