package rowing.activity.domain;

import lombok.Data;
import rowing.activity.domain.entities.Training;
import rowing.activity.domain.utils.Builder;
import rowing.commons.Position;

import java.util.*;

@Data
public class TrainingBuilder implements Builder {
    private UUID id;
    private String owner;
    private String name;
    private List<Position> positions;
    private String type;
    private Date start;
    private List<String> applicants;
    private String boatType;

    public Training build() {
        return new Training(id, owner, name, type, start, positions, applicants, boatType);
    }
}
