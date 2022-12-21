package rowing.notification.domain.notification;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mail.javamail.JavaMailSender;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EmailServiceTest {

    Notification notification;
    EmailService emailService;
    JavaMailSender mailSender;

    @BeforeEach
    void setup() {
        emailService = new EmailService();
        mailSender = mock(JavaMailSender.class);
        emailService.setMailSender(mailSender);
        emailService.setEmailAddress("me@gmail.com");


        // mocking
        notification = mock(Notification.class);
        when(notification.getDestinationEmail()).thenReturn("someEmail@gmail.com");
        when(notification.retrieveSubject()).thenReturn("subj");
        when(notification.retrieveBody()).thenReturn("body");
    }

    @Test
    void sendEmail() {
        emailService.sendEmail(notification);
    }

    @Test
    void exception1() {
        assertThrows(IllegalArgumentException.class, () -> {
            emailService.sendEmail(null);
        });
    }

    @Test
    void exception2() {
        emailService.setEmailAddress(null);
        assertThrows(IllegalArgumentException.class, () -> {
            emailService.sendEmail(notification);
        });
    }

    @Test
    void exception3() {
        when(notification.getDestinationEmail()).thenReturn(null);
        assertThrows(IllegalArgumentException.class, () -> {
            emailService.sendEmail(null);
        });

        when(notification.getDestinationEmail()).thenReturn("someEmailgmail.com");
        assertThrows(IllegalArgumentException.class, () -> {
            emailService.sendEmail(null);
        });
    }

    @Test
    void exception4() {
        when(notification.retrieveBody()).thenReturn(null);
        assertThrows(IllegalArgumentException.class, () -> {
            emailService.sendEmail(notification);
        });
    }

    @Test
    void exception5() {
        when(notification.retrieveSubject()).thenReturn(null);
        assertThrows(IllegalArgumentException.class, () -> {
            emailService.sendEmail(notification);
        });
    }
}