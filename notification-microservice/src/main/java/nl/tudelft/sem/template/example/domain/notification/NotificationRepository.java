package nl.tudelft.sem.template.example.domain.notification;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * A DDD repository for querying and persisting notification entities.
 */
@Repository
public interface NotificationRepository extends JpaRepository<Notification, Integer> {
    /**
     * Find all notifications by user id
     */
    Optional<List<Notification>> findByUserId(int userId);

    /**
     * Delete all notifications by user id
     */
    boolean deleteAllByUserId(int userId);

    /**
     * Delete all notifications by activity id
     */
    boolean deleteAllByActivityId(int activityId);

    //TODO
    //validate the request

}
