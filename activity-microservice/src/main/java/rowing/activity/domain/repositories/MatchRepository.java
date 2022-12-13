package rowing.activity.domain.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rowing.activity.domain.entities.Activity;
import rowing.activity.domain.entities.Match;

import java.util.Optional;
import java.util.UUID;

/**
 * Repository to keep matching information : user to activity and which position.
 */
@Repository
public interface MatchRepository extends JpaRepository<Match, UUID> {

    Optional<Activity> findActivityByActivityId(UUID activityId);

    boolean existsByActivityId(UUID activityId);
}