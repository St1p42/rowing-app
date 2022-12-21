package rowing.notification.domain.notification.strategy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import rowing.notification.domain.notification.EmailService;
import rowing.notification.domain.notification.Notification;

@Component
public class EmailStrategy implements Strategy {
    @Autowired
    transient EmailService emailService;

    @Override
    public StrategyName getStrategyName() {
        return StrategyName.EMAIL;
    }

    @Override
    public void notifyUser(Notification notification) {
        emailService.sendEmail(notification);
    }
}
