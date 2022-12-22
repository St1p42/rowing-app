package rowing.activity.domain;

import lombok.Data;
import rowing.activity.domain.entities.Competition;
import rowing.activity.domain.utils.Builder;
import rowing.commons.Gender;
import rowing.commons.Position;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Data
public class CompetitionBuilder implements Builder {
    private UUID id;
    private String owner;
    private String name;
    private String type;
    private Date start;
    private Gender gender;
    private String organisation;
    private List<Position> positions;
    private List<String> applicants;

    public Competition build() {
        return new Competition(id, owner, name, type, start, gender, organisation, positions, applicants);
    }
}
