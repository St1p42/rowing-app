package rowing.user.domain.user;

import java.util.Date;
import lombok.Data;
import lombok.NoArgsConstructor;

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
