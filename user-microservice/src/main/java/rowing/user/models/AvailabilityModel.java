package rowing.user.models;

import lombok.Data;

import java.time.DayOfWeek;
import java.time.LocalTime;

@Data
public class AvailabilityModel {

    private String day;
    private String start;
    private String end;

    /**
     * Constructs a model based on string arguments from the user.
     *
     * @param day - day of the availability.
     * @param start - start time of the availability.
     * @param end - end time of the availability.
     */
    public AvailabilityModel(String day, String start, String end) {
        this.day = day;
        this.start = start;
        this.end = end;
    }
}
