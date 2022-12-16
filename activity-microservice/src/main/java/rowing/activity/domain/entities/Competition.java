package rowing.activity.domain.entities;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.*;
import rowing.commons.Gender;
import rowing.commons.Position;
import rowing.commons.entities.CompetitionDTO;

import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Inheritance(strategy = InheritanceType.JOINED)
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Data
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
public class Competition extends Activity<CompetitionDTO> {

    private Gender gender;
    private String organisation;

    public Competition(CompetitionDTO dto) {
        super(dto);
    }

    /**
     * Constructor for activity.
     *
     * @param id of the activity
     * @param owner id of the owner
     * @param name name of the activity
     * @param positions list of positions to be filled
     * @param type training or competition
     * @param start date it starts at
     * @param gender gender requirements
     * @param organisation organization requirements
     */
    public Competition(UUID id, UUID owner, String name, String type,
                       Date start, List<Position> positions, Gender gender, String organisation, List<UUID> applicants) {
        super(id, owner, name, type, start, positions, applicants);
        this.gender = gender;
        this.organisation = organisation;
    }

    /**
     * Returns the DTO of this competition.
     *
     * @return the DTO of this competition.
     */
    @Override
    public CompetitionDTO getDto() {
        return new CompetitionDTO((CompetitionDTO) super.toDto());
    }
}