package rowing.user.domain.user;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * A DDD repository for quering and persisting user aggregate roots.
 */
@Repository
public interface UserRepository extends JpaRepository<User, String> {
    /**
     * Find user by Username.
     */
    Optional<User> findByUserId(int userId);

    /**
     * Check if an existing user already uses a Username.
     */
    boolean existsByUserId(int userId);
}
