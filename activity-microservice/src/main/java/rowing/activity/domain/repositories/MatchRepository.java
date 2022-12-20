package rowing.activity.domain.repositories;

import org.h2.engine.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rowing.activity.domain.entities.Activity;
import rowing.activity.domain.entities.Match;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository to keep matching information : user to activity and which position.
 */
@Repository
public interface MatchRepository extends JpaRepository<Match, UUID> {

    Optional<Match> findActivityByActivityId(UUID activityId);

    Optional<Match> findUserByUserId(UUID userId);

    boolean existsByActivityId(UUID activityId);

    List<Match> findAllByActivityId(UUID activityID);
}