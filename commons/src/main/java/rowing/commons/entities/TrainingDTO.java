package rowing.commons.entities;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import rowing.commons.entities.utils.Views;

@EqualsAndHashCode(callSuper = true)
@Data
@ToString(callSuper = true)
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
@JsonView(Views.Public.class)
public class TrainingDTO extends ActivityDTO {

    /**
     * Empty constructor.
     */
    public TrainingDTO() {
        super();
    }

    public TrainingDTO(ActivityDTO superDTO) {
        super(superDTO);
    }
}
