package rowing.activity.domain.entities;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.*;
import rowing.commons.Position;
import rowing.commons.entities.TrainingDTO;

import javax.persistence.*;
import java.util.*;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Inheritance(strategy = InheritanceType.JOINED)
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Data
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
public class Training extends Activity {

    Map<UUID, Position> participants;

    public Training(TrainingDTO dto) {
        super(dto);
    }

    public Training(UUID id, UUID owner, String name, List<Position> positions, String type, Date start, Map<UUID, Position> participants) {
        super(id, owner, name, type, start, positions);
        this.participants = participants;
    }
    /**
     * Returns the DTO of this training.
     *
     * @return the DTO of this training.
     */
    @Override
    public TrainingDTO getDTO() {
        return new TrainingDTO((TrainingDTO) super.toDTO());
    }
}
