package rowing.activity.domain;

import lombok.Data;
import org.springframework.data.util.Pair;
import rowing.activity.domain.entities.Training;
import rowing.commons.Position;

import java.util.*;

@Data
public class TrainingBuilder implements Builder {
    private UUID id;
    private UUID owner;
    private String name;
    private List<Position> positions;
    private String type;
    private Date start;
    private List<String> applicants;

    public Training build() {
        return new Training(id, owner, name, type, start, positions, applicants);
    }
}
