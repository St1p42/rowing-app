package rowing.user.domain;

import org.junit.jupiter.api.Test;
import rowing.user.domain.user.AvailabilityIntervals;

import java.time.DateTimeException;
import java.time.DayOfWeek;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class AvailabilityIntervalsTest {

    @Test
    public void testAvailabilityIntervals() {
        String time1 = "10:00";
        String time2 = "11:00";
        String day = "Monday";
        AvailabilityIntervals i = new AvailabilityIntervals(day, time1, time2);
        assertThat(i.toString()).isEqualTo("MONDAY 10:00-11:00");
    }

    @Test
    public void testNegativeDuration() {
        String time1 = "11:00";
        String time2 = "10:00";
        String day = "Monday";
        assertThrows(IllegalArgumentException.class,
                () -> {
                AvailabilityIntervals i = new AvailabilityIntervals(day, time1, time2);
                });
    }

    @Test
    public void testNotGoodFirstFormat() {
        String time1 = "11";
        String time2 = "11:30";
        String day = "Monday";
        assertThrows(IllegalArgumentException.class,
                () -> {
                AvailabilityIntervals i = new AvailabilityIntervals(day, time1, time2);
                });
    }

    @Test
    public void testNotGoodSecondFormat() {
        String time1 = "11:30";
        String time2 = "12:30:11";
        String day = "Monday";
        assertThrows(IllegalArgumentException.class,
                () -> {
                AvailabilityIntervals i = new AvailabilityIntervals(day, time1, time2);
                });
    }

    @Test
    public void testBothNotGoodFormat() {
        String time1 = "01:5";
        String time2 = "30:70";
        String day = "Monday";
        assertThrows(IllegalArgumentException.class,
                () -> {
                AvailabilityIntervals i = new AvailabilityIntervals(day, time1, time2);
                });
    }

    @Test
    public void testTimeFromDatabase() {
        String time = "MONDAY 10:00-11:00";
        AvailabilityIntervals i = new AvailabilityIntervals(time);
        assertThat(i.getStartInterval()).isEqualTo("10:00");
        assertThat(i.getEndInterval()).isEqualTo("11:00");
        assertThat(i.getDay()).isEqualTo(DayOfWeek.MONDAY);
    }

    @Test
    public void testNotValidDay() {
        String time1 = "10:00";
        String time2 = "11:00";
        String day = "Luni";
        assertThrows(IllegalArgumentException.class,
                () -> {
                AvailabilityIntervals i = new AvailabilityIntervals(day, time1, time2);
                });
    }

    @Test
    public void testLowerCaseDay() {
        String time1 = "10:00";
        String time2 = "11:00";
        String day = "monday";
        AvailabilityIntervals i = new AvailabilityIntervals(day, time1, time2);
        assertThat(i.toString()).isEqualTo("MONDAY 10:00-11:00");
    }

    @Test
    public void testUpperCaseDay() {
        String time1 = "10:00";
        String time2 = "11:00";
        String day = "MONDAY";
        AvailabilityIntervals i = new AvailabilityIntervals(day, time1, time2);
        assertThat(i.toString()).isEqualTo("MONDAY 10:00-11:00");
    }

    @Test
    public void testRandomCaseDay() {
        String time1 = "10:00";
        String time2 = "11:00";
        String day = "MoNdAy";
        AvailabilityIntervals i = new AvailabilityIntervals(day, time1, time2);
        assertThat(i.toString()).isEqualTo("MONDAY 10:00-11:00");
    }

    @Test
    public void testDayNull() {
        String time1 = "10:00";
        String time2 = "11:00";
        String day = null;
        assertThrows(IllegalArgumentException.class,
                () -> {
                AvailabilityIntervals i = new AvailabilityIntervals(day, time1, time2);
                });
    }

    @Test
    public void testFirstTimeNull() {
        String time1 = null;
        String time2 = "11:00";
        String day = "Monday";
        assertThrows(IllegalArgumentException.class,
                () -> {
                AvailabilityIntervals i = new AvailabilityIntervals(day, time1, time2);
                });
    }

    @Test
    public void testSecondTimeNull() {
        String time1 = "10:00";
        String time2 = null;
        String day = "Monday";
        assertThrows(IllegalArgumentException.class,
                () -> {
                AvailabilityIntervals i = new AvailabilityIntervals(day, time1, time2);
                });
    }
}
