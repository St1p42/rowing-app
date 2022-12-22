package rowing.user.services;

import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rowing.commons.AvailabilityIntervals;
import rowing.user.domain.user.AvailabilityNotFoundException;
import rowing.user.domain.user.User;
import rowing.user.domain.user.UserRepository;
import rowing.user.domain.user.utils.UserNotFoundException;

import java.time.DateTimeException;
import java.util.List;
import java.util.Optional;

@Service
@NoArgsConstructor
public class AvailabilityService {

    @Autowired
    private UserRepository userRepository;

    /**
     * Initializes the user repository.
     *
     * @param userRepository - the jpa repository
     */
    public AvailabilityService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Returns a user based on its id.
     *
     * @param userId - the id of the user we are interested in.
     * @return user - the user object with the specified userId
     */
    public User findUserById(String userId)
            throws UserNotFoundException {
        Optional<User> u = userRepository.findByUserId(userId);
        User user = u.get();
        //return null;
        return user;
    }

    /**
     * Adds the specified availability to the user with userId.
     *
     * @param day - day of the availability
     * @param startTime - start time of the availability.
     * @param endTime - end time of the availability.
     * @param userId - userId of the required user
     * @return u - user with the updated availability.
     * @throws IllegalArgumentException - exception if availability does not respect the constraints
     * @throws DateTimeException - exception if the format of the time is incorrect
     */
    public User addAvailability(String day, String startTime, String endTime, String userId)
            throws IllegalArgumentException, DateTimeException, UserNotFoundException {
        AvailabilityIntervals interval = new AvailabilityIntervals(day, startTime, endTime);
        User u = findUserById(userId);
        List<AvailabilityIntervals> intervals = u.getAvailability();
        intervals.add(interval);
        return u;
    }

    /**
     * Removes the specified availability to the user with userId.
     *
     * @param day - day of the availability
     * @param startTime - start time of the availability.
     * @param endTime - end time of the availability.
     * @param userId - userId of the required user
     * @return u - user with the updated availability.
     * @throws AvailabilityNotFoundException - exception if the required availability is not found
     * @throws IllegalArgumentException - exception if availability does not respect the constraints
     * @throws DateTimeException - exception if the format of the time is incorrect
     */
    public User removeAvailability(String day, String startTime, String endTime, String userId)
            throws AvailabilityNotFoundException, IllegalArgumentException, DateTimeException, UserNotFoundException {
        AvailabilityIntervals interval = new AvailabilityIntervals(day, startTime, endTime);
        User u = findUserById(userId);
        List<AvailabilityIntervals> intervals = u.getAvailability();
        if (intervals.contains(interval) == true) {
            intervals.remove(interval);
            return u;
        }
        throw new AvailabilityNotFoundException(interval);
    }

    /**
     * Edits the specified availability to a new one of the user with userId.
     *
     * @param dayOld - old day of the availability
     * @param startTimeOld - old start time of the availability.
     * @param endTimeOld - old time of the availability.
     * @param dayNew - new day of the availability
     * @param startTimeNew - new start time of the availability.
     * @param endTimeNew - new end time of the availability.
     * @param userId - userId of the required user
     * @return u - user with the updated availability.
     * @throws AvailabilityNotFoundException - exception if the required availability is not found
     * @throws IllegalArgumentException - exception if availability does not respect the constraints
     * @throws DateTimeException - exception if the format of the time is incorrect
     */
    public User editAvailability(String dayOld, String startTimeOld, String endTimeOld,
                                 String dayNew, String startTimeNew, String endTimeNew, String userId)
            throws AvailabilityNotFoundException, IllegalArgumentException, DateTimeException, UserNotFoundException {
        AvailabilityIntervals intervalOld = new AvailabilityIntervals(dayOld, startTimeOld, endTimeOld);
        AvailabilityIntervals intervalNew;
        intervalNew = new AvailabilityIntervals(dayNew, startTimeNew, endTimeNew);
        User u = findUserById(userId);
        List<AvailabilityIntervals> intervals = u.getAvailability();
        if (intervals.contains(intervalOld) == true) {
            intervals.remove(intervalOld);
            intervals.add(intervalNew);
            return u;
        }
        throw new AvailabilityNotFoundException(intervalOld);
    }

}
