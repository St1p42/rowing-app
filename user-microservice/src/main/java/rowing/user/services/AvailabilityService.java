package rowing.user.services;

import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rowing.user.domain.user.AvailabilityNotFoundException;
import rowing.user.domain.user.User;
import rowing.user.domain.user.UserRepository;
import rowing.user.domain.user.AvailabilityIntervals;

import java.time.DateTimeException;
import java.time.DayOfWeek;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static java.util.Locale.forLanguageTag;

@Service
@NoArgsConstructor
public class AvailabilityService {

    @Autowired
    private UserRepository userRepository;

    public AvailabilityService(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    public List<Set<AvailabilityIntervals>> findAvailabilitiesById(String userId){
        Optional<User> u = userRepository.findByUserId(userId);
        User user = u.get();
        //return null;
        return user.getAvailability();
    }

    public void addAvailability(String time, String day, String userId) throws IllegalArgumentException, DateTimeException {
        AvailabilityIntervals interval = new AvailabilityIntervals(time);
        List<Set<AvailabilityIntervals>> intervals = findAvailabilitiesById(userId);
        int representation = convertDayToInt(day);
        intervals.get(representation).add(interval);
    }

    public void removeAvailability(String time, String day, String userId) throws AvailabilityNotFoundException, IllegalArgumentException, DateTimeException {
        AvailabilityIntervals interval = new AvailabilityIntervals(time);
        List<Set<AvailabilityIntervals>> intervals = findAvailabilitiesById(userId);
        int representation = convertDayToInt(day);
        if(intervals.get(representation).contains(interval) == true) {
            intervals.get(representation).remove(interval);
        }
        throw new AvailabilityNotFoundException(interval);
    }

    public void editAvailability(String timeOld, String timeNew, String day, String userId) throws AvailabilityNotFoundException, IllegalArgumentException, DateTimeException {
        List<Set<AvailabilityIntervals>> intervals = findAvailabilitiesById(userId);
        removeAvailability(timeOld, day, userId);
        addAvailability(timeNew, day, userId);
    }

    public int convertDayToInt(String day) throws DateTimeException{
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE", forLanguageTag("en"));
        TemporalAccessor accessor = formatter.parse(day);
        DayOfWeek dayOfWeek = DayOfWeek.from(accessor);
        return dayOfWeek.getValue() - 1;
    }
}
