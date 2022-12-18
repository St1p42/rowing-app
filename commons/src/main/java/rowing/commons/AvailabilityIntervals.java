package rowing.commons;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
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
