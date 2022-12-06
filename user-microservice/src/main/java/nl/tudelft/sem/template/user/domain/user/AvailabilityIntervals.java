package nl.tudelft.sem.template.user.domain.user;

import java.util.Date;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AvailabilityIntervals {
    private Date startInterval;
    private Date endInterval;

    public AvailabilityIntervals(Date startInterval, Date endInterval) {
        this.startInterval = startInterval;
        this.endInterval = endInterval;
    }

    public int getIntervalLength(AvailabilityIntervals interval) {
        //TODO
        return 0;
    }
}
