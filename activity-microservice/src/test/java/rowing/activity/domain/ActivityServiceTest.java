package rowing.activity.domain;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import rowing.activity.domain.entities.Activity;
import rowing.activity.domain.utils.Builder;
import rowing.activity.services.ActivityService;
import rowing.commons.AvailabilityIntervals;
import rowing.commons.Gender;
import rowing.commons.Position;
import rowing.commons.entities.MatchingDTO;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

public class ActivityServiceTest {

    Activity amateurTraining;
    Activity amateurCompetition;
    Date amateurTrainingDate;
    Date amateurCompetitionDate;
    MatchingDTO match;
    UUID trainingId;
    UUID competitionId;

    /**
     * Function that inits the basic activity.
     *
     * @throws ParseException exception for wrong format
     */
    @BeforeEach
    public void init() throws ParseException {

        String dateString = "26-09-3043 14:05:05";
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
        amateurTrainingDate = formatter.parse(dateString);
        trainingId = UUID.randomUUID();

        dateString = "27-09-3043 16:05:05"; // Creating new competition details
        formatter = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
        amateurCompetitionDate = formatter.parse(dateString);
        competitionId = UUID.randomUUID();

        Builder trainingBuilder = new TrainingBuilder();
        Director director = new Director();

        List<Position> positionList = new ArrayList<>();
        positionList.add(Position.COACH);
        positionList.add(Position.COX);
        List<String> applicantList = new ArrayList<>();

        director.constructTraining((TrainingBuilder) trainingBuilder, UUID.randomUUID(),
                "Admin", "Amateur Training", "Training",
                amateurTrainingDate, positionList, applicantList);
        amateurTraining = trainingBuilder.build();

        Builder competitionBuilder = new CompetitionBuilder();
        director.constructCompetition((CompetitionBuilder) competitionBuilder, UUID.randomUUID(),
                "Admin", "Amateur Competition", "Competition",
                amateurCompetitionDate,  Gender.MALE, "TUDelft", positionList, applicantList);
        amateurCompetition = competitionBuilder.build();

        List<AvailabilityIntervals> availability = new ArrayList<AvailabilityIntervals>();
        availability.add(new AvailabilityIntervals("wednesday", "14:05", "14:06"));
        availability.add(new AvailabilityIntervals("thursday", "16:05", "16:06"));
        match = new MatchingDTO(UUID.randomUUID(), null,
                "Admin", Position.COX, Gender.MALE, true, "TUDelft",
                availability, null);
    }

    @Test
    public void availabilityCheckTestBoundaryLeftTrue() {
        assertTrue(ActivityService.checkAvailability(amateurTraining, match.getAvailability()));
    }

    @Test
    public void availabilityCheckTestFalse() {
        List<AvailabilityIntervals> newAvailability = new ArrayList<>();
        newAvailability.add(new AvailabilityIntervals("thursday", "16:03", "16:04"));
        match.setAvailability(newAvailability);
        assertTrue(!ActivityService.checkAvailability(amateurTraining, match.getAvailability()));
    }

    @Test
    public void availabilityCheckTestBoundaryRightFalse() { // Interval is inclusive only for start
        List<AvailabilityIntervals> newAvailability = new ArrayList<>();
        newAvailability.add(new AvailabilityIntervals("thursday", "16:04", "16:05"));
        match.setAvailability(newAvailability);
        assertTrue(!ActivityService.checkAvailability(amateurTraining, match.getAvailability()));
    }

    @Test
    public void availabilityCheckTestDifferentDay() { // Interval is inclusive only for start
        List<AvailabilityIntervals> newAvailability = new ArrayList<>();
        newAvailability.add(new AvailabilityIntervals("monday", "16:04", "16:10"));
        match.setAvailability(newAvailability);
        assertTrue(!ActivityService.checkAvailability(amateurTraining, match.getAvailability()));
    }
}
