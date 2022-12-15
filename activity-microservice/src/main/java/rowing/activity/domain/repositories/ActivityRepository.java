package rowing.activity.domain.repositories;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rowing.activity.domain.entities.Activity;

/**
 * A DDD repository for quering and persisting user aggregate roots.
 * Stores information about activities
 */
@Repository
public interface ActivityRepository extends JpaRepository<Activity, String> {
    /**
     * Find activity by name.
     */
    Optional<Activity> findActivityById(UUID id);

    boolean existsById(UUID id);
}