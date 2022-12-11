package rowing.commons.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import rowing.commons.Position;
import rowing.commons.entities.utils.DTO;
import rowing.commons.entities.utils.Views;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@Data
@NoArgsConstructor
@ToString(callSuper = true)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
@JsonSubTypes({
        @JsonSubTypes.Type(value = ActivityDTO.class, name = "DefiniteGameDTO")
})
@JsonView(Views.Public.class)
public class ActivityDTO implements DTO {

    private UUID id;
    private UUID owner;
    private String type;
    private String name;
    private Date start;
    private List<Position> positions;

    public ActivityDTO(ActivityDTO activity) {
        this.id = activity.getId();
        this.owner = activity.getOwner();
        this.name = activity.getName();
        this.type = activity.getType();
        this.start = activity.getStart();
        this.positions = activity.getPositions();
    }
}
