package rowing.commons;

import java.text.ParseException;
import java.time.DateTimeException;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.time.temporal.TemporalAccessor;
import lombok.Data;
import lombok.NoArgsConstructor;
import static java.time.temporal.ChronoUnit.MINUTES;
import static java.util.Locale.forLanguageTag;

@Data
@NoArgsConstructor
public class AvailabilityIntervals {
    private LocalTime startInterval;
    private LocalTime endInterval;
    private DayOfWeek day;

    /**
     * Constructor that turns strings given by the user into LocalTimes and day of week.
     *
     * @param dayOfWeek - day represented as enum
     * @param startInterval - the time the interval starts
     * @param endInterval - the time the interval ends
     * @throws IllegalArgumentException - if the format is not right
     */
    public AvailabilityIntervals(String dayOfWeek, String startInterval, String endInterval)
            throws IllegalArgumentException {
        LocalTime d1;
        LocalTime d2;
        DayOfWeek d;
        if (dayOfWeek == null || startInterval == null || endInterval == null) {
            throw new IllegalArgumentException();
        }
        DateTimeFormatter formatter = new DateTimeFormatterBuilder().parseCaseInsensitive().appendPattern("EEEE")
                .toFormatter(forLanguageTag("en"));
        try {
            d1 = convertToTime(startInterval);
            d2 = convertToTime(endInterval);
            TemporalAccessor accessor = formatter.parse(dayOfWeek);
            d = DayOfWeek.from(accessor);
        } catch (ParseException | DateTimeException e) {
            throw new IllegalArgumentException();
        }

        long duration = MINUTES.between(d1, d2);
        if (duration <= 0) {
            throw new IllegalArgumentException("Duration should be bigger than 0");
        }
        this.startInterval = d1;
        this.endInterval = d2;
        this.day = d;
    }

    /**
     * Constructor for converting strings from database into the object.
     *
     * @param interval - string with all the information from the database
     */
    public AvailabilityIntervals(String interval) {
        String[] comp = interval.split(" ");
        String[] intervals = comp[1].split("-");
        DateTimeFormatter formatter = new DateTimeFormatterBuilder().parseCaseInsensitive().appendPattern("EEEE")
                .toFormatter(forLanguageTag("en"));
        try {
            this.startInterval = LocalTime.parse(intervals[0]);
            this.endInterval = LocalTime.parse(intervals[1]);
            TemporalAccessor accessor = formatter.parse(comp[0]);
            this.day = DayOfWeek.from(accessor);
        } catch (DateTimeParseException e) {
            return;
        }
    }

    /**
     * Converts a string to LocalTime and checks for correct format.
     *
     * @param time - the string representing a time
     * @return d - a LocalTime object
     * @throws ParseException - exception if the format is incorrect.
     */
    public LocalTime convertToTime(String time) throws ParseException {
        //time += ":00";
        int limit = 5;
        if (time.length() != limit) {
            throw new ParseException("INCORRECT FORMAT", time.length());
        }
        LocalTime d = LocalTime.parse(time);
        return d;
    }

    @Override
    public String toString() {
        return this.day + " " + this.startInterval.toString() + "-" + this.endInterval.toString();
    }
}
