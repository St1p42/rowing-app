package rowing.user.domain.user.utils;

public class UserNotFoundException extends Exception {
    static final long serialVersionUID = -3387516993124229948L;

    public UserNotFoundException(String userId) {
        super(userId + "was not found");
    }
}
