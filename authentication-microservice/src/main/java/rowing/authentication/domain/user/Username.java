package rowing.authentication.domain.user;

import lombok.EqualsAndHashCode;

/**
 * A DDD value object representing a Username in our domain.
 */
@EqualsAndHashCode
public class Username {
    private final transient String usernameValue;

    public Username(String username) {
        // validate Username
        this.usernameValue = username;
    }

    @Override
    public String toString() {
        return usernameValue;
    }
}
