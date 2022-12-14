package rowing.user.domain.user;

public class AvailabilityNotFoundException extends Exception {

    public AvailabilityNotFoundException(AvailabilityIntervals interval) {
        super(interval.toString() + "was not found");
    }
}
