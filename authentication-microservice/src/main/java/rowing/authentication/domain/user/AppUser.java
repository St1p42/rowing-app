package rowing.authentication.domain.user;

import java.util.Objects;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.NoArgsConstructor;
import rowing.authentication.domain.HasEvents;

/**
 * A DDD entity representing an application user in our domain.
 */
@Entity
@Table(name = "users")
@NoArgsConstructor
public class AppUser extends HasEvents {
    /**
     * Identifier for the application user.
     */
    @Id
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "username", nullable = false, unique = true)
    @Convert(converter = UsernameAttributeConverter.class)
    private Username username;

    @Column(name = "password_hash", nullable = false)
    @Convert(converter = HashedPasswordAttributeConverter.class)
    private HashedPassword password;

    /**
     * Create new application user.
     *
     * @param username The Username for the new user
     * @param password The password for the new user
     */
    public AppUser(Username username, HashedPassword password) {
        this.username = username;
        this.password = password;
        this.id = UUID.randomUUID();
        this.recordThat(new UserWasCreatedEvent(username));
    }

    public void changePassword(HashedPassword password) {
        this.password = password;
        this.recordThat(new PasswordWasChangedEvent(this));
    }

    public Username getUsername() {
        return username;
    }

    public HashedPassword getPassword() {
        return password;
    }

    /**
     * Equality is only based on the identifier.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        AppUser appUser = (AppUser) o;
        return id == (appUser.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username);
    }
}
