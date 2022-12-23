package rowing.activity.domain.entities;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import rowing.commons.Gender;
import rowing.commons.Position;
import rowing.commons.entities.ActivityDTO;
import rowing.commons.entities.TrainingDTO;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ActivityTest {
    ActivityDTO dto;
    UUID id;
    Date startDate;

    @BeforeEach
    void setup() {
        startDate = new Date();
        id = UUID.randomUUID();
        String owner = "Khalit";
        String name = "Test Activity";
        String type = "Training";

        List<Position> positionList = new ArrayList<>();
        positionList.add(Position.PORT);
        positionList.add(Position.COX);

        List<String> applicantList = new ArrayList<>();
        String boatType = "Boat1337";
        dto = new ActivityDTO(id, owner, name, type, startDate, positionList, applicantList, boatType);
    }

    @Test
    void activityDtoNotNullId() {
        TrainingDTO trainingDTO = new TrainingDTO(dto);
        Training training = new Training(trainingDTO);
        assertNotNull(training);
        assertEquals(0, training.getApplicants().size());
        assertSame(training.getId(), id);
        assertSame("Khalit", training.getOwner());
        assertSame("Test Activity", training.getName());
        assertSame("Training", training.getType());
        assertEquals(training.getPositions().get(0), Position.PORT);
        assertEquals(training.getPositions().get(1), Position.COX);
        assertSame("Boat1337", training.getBoatType());
    }

    @Test
    void activityDtoNullId() {
        TrainingDTO trainingDTO = new TrainingDTO(dto);
        trainingDTO.setId(null);
        Training training = new Training(trainingDTO);
        assertNotNull(training);
        assertEquals(0, training.getApplicants().size());
        assertNotNull(training.getId());
        assertSame("Khalit", training.getOwner());
        assertSame("Test Activity", training.getName());
        assertSame("Training", training.getType());
        assertEquals(training.getPositions().get(0), Position.PORT);
        assertEquals(training.getPositions().get(1), Position.COX);
        assertSame("Boat1337", training.getBoatType());
    }
}