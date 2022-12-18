package rowing.notification.domain.notification.strategy;

import org.springframework.stereotype.Component;
import rowing.notification.domain.notification.Notification;

@Component
public class KafkaStrategy implements Strategy{
    @Override
    public StrategyName getStrategyName() {
        return StrategyName.KAFKA;
    }

    @Override
    public void notifyUser(Notification notification) {

    }
}
