package rowing.activity.domain.entities;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.*;
import rowing.commons.Requirement;
import rowing.commons.Position;
import rowing.commons.entities.CompetitionDTO;

import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Inheritance(strategy = InheritanceType.JOINED)
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Data
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
public class Competition extends Activity {

    Requirement requirement;

    public Competition(CompetitionDTO dto) {
        super(dto);
    }

    public Competition(UUID id, UUID owner, String name, List<Position> positions, String type, Date start, Requirement requirement) {
        super(id, owner, name, type, start, positions);
        this.requirement = requirement;
    }

    /**
     * Returns the DTO of this training.
     *
     * @return the DTO of this training.
     */
    @Override
    public CompetitionDTO getDTO() {
        return new CompetitionDTO((CompetitionDTO) super.toDTO());
    }
}