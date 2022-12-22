package rowing.activity.domain.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rowing.activity.domain.entities.Match;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository to keep matching information : user to activity and which position.
 */
@Repository
public interface MatchRepository extends JpaRepository<Match, UUID> {

    Optional<Match> findByActivityId(UUID activityId);

    Optional<Match> findByUserId(String userId);

    Optional<Match> findByActivityIdAndUserId(UUID activityId, String userId);

    boolean existsByActivityId(UUID activityId);

    boolean existsByActivityIdAndUserId(UUID activityId, String userID);

    List<Match> findAllByActivityId(UUID activityID);
}