package rowing.user.domain.user;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * A DDD repository for quering and persisting user aggregate roots.
 */
@Repository
public interface UserRepository2 extends JpaRepository<User, String> {
    /**
     * Find user by NetID.
     */
    Optional<User> findByUserId(int userId);

    /**
     * Check if an existing user already uses a NetID.
     */
    boolean existsByUserId(int userId);
}
