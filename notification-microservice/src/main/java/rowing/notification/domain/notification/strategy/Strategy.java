package rowing.notification.domain.notification.strategy;

import rowing.notification.domain.notification.Notification;

public interface Strategy {
    void notifyUser(Notification notification);

    StrategyName getStrategyName();
}
