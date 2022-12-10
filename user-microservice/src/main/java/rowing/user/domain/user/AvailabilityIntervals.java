package rowing.user.domain.user;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.time.temporal.TemporalUnit;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import javassist.LoaderClassPath;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.tomcat.jni.Local;

import static java.time.temporal.ChronoUnit.MINUTES;

@Data
@NoArgsConstructor
public class AvailabilityIntervals {
    private LocalTime startInterval;
    private LocalTime endInterval;

    public AvailabilityIntervals(String startInterval, String endInterval) throws IllegalArgumentException {
        LocalTime d1 = null;
        LocalTime d2 = null;
        try{
            d1 = convertToDate(startInterval);
            d2 = convertToDate(endInterval);
        } catch (ParseException e) {
            throw new IllegalArgumentException();
        }
        long duration = MINUTES.between(d1, d2);
        if(duration <= 0)
            throw new IllegalArgumentException("Duration should be bigger than 0");
        this.startInterval = d1;
        this.endInterval = d2;
    }

    public AvailabilityIntervals(String interval){
        String[] intervals = interval.split("-");
        try {
            this.startInterval = LocalTime.parse(intervals[0]);
            this.endInterval = LocalTime.parse(intervals[1]);
        } catch (DateTimeParseException e){
            return;
        }
    }
    public LocalTime convertToDate(String time) throws ParseException {
        //time += ":00";
        if(time.length() != 5)
            throw new ParseException("INCORRECT FORMAT", time.length());
        LocalTime d = LocalTime.parse(time);
        return d;
    }
    @Override
    public String toString(){
        return this.startInterval.toString() + "-" + this.endInterval.toString();
    }

}
