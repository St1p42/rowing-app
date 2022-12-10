package rowing.user.domain.AvailabilityIntervals;

import org.junit.jupiter.api.Test;
import rowing.user.domain.user.AvailabilityIntervals;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
public class AvailabilityIntervalsTest {

    @Test
    public void testAvailabilityIntervals(){
        String time1 = "10:00";
        String time2 = "11:00";
        AvailabilityIntervals i = new AvailabilityIntervals(time1, time2);
        assertThat(i.toString()).isEqualTo("10:00-11:00");
    }

    @Test
    public void testnegativeDuration(){
        String time1 = "11:00";
        String time2 = "10:00";
        assertThrows(IllegalArgumentException.class, () -> {AvailabilityIntervals i = new AvailabilityIntervals(time1, time2);});
    }

    @Test
    public void testNotGoodFirstFormat(){
        String time1 = "11";
        String time2 = "11:30";
        assertThrows(IllegalArgumentException.class, () -> {AvailabilityIntervals i = new AvailabilityIntervals(time1, time2);});
    }

    @Test
    public void testNotGoodSecondFormat(){
        String time1 = "11:30";
        String time2 = "12:30:11";
        assertThrows(IllegalArgumentException.class, () -> {AvailabilityIntervals i = new AvailabilityIntervals(time1, time2);});
    }
    @Test
    public void testBothNotGoodFormat(){
        String time1 = "01:5";
        String time2 = "30:70";
        assertThrows(IllegalArgumentException.class, () -> {AvailabilityIntervals i = new AvailabilityIntervals(time1, time2);});
    }

    @Test
    public void testTimeFromDatabase(){
        String time = "10:00-11:00";
        AvailabilityIntervals i = new AvailabilityIntervals(time);
        assertThat(i.getStartInterval()).isEqualTo("10:00");
        assertThat(i.getEndInterval()).isEqualTo("11:00");
    }
}
