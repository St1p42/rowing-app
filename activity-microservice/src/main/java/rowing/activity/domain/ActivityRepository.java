package rowing.activity.domain;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * A DDD repository for quering and persisting user aggregate roots.
 */
@Repository
public interface ActivityRepository extends JpaRepository<Activity, String> {
    /**
     * Find activity by name.
     */
    Optional<Activity> findActivityByActivityName(String activityName);

    boolean existsByActivityId(int activityId);
}