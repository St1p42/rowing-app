package rowing.activity.domain.entities;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.*;
import rowing.commons.Position;
import rowing.commons.entities.TrainingDTO;

import javax.persistence.*;
import java.util.*;

@Entity
@AllArgsConstructor
@Inheritance(strategy = InheritanceType.JOINED)
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Data
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
public class Training extends Activity<TrainingDTO> {

    public Training(TrainingDTO dto) {
        super(dto);
    }

    public Training(UUID id, String owner, String name, String type,
                    Date start, List<Position> positions, List<String> applicants, String boatType) {
        super(id, owner, name, type, start, positions, applicants, boatType);
    }

    /**
     * Returns the DTO of this training.
     *
     * @return the DTO of this training.
     */
    @Override
    public TrainingDTO getDto() {
        return new TrainingDTO(super.toDto());
    }
}
