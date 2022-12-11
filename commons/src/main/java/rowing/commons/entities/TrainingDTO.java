package rowing.commons.entities;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.*;
import rowing.commons.Position;
import rowing.commons.entities.utils.Views;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@AllArgsConstructor
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@ToString(callSuper = true)
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
@JsonSubTypes({
        @JsonSubTypes.Type(value = TrainingDTO.class, name = "TrainingDTO")
})
@JsonView(Views.Public.class)
public class TrainingDTO extends ActivityDTO{


    public TrainingDTO(ActivityDTO superDTO) {
        super(superDTO);
    }
}
