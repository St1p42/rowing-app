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
import rowing.commons.Requirement;
import rowing.commons.entities.utils.Views;

import java.util.Map;
import java.util.UUID;

@AllArgsConstructor
@Data
@NoArgsConstructor
@ToString(callSuper = true)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
@JsonSubTypes({
        @JsonSubTypes.Type(value = CompetitionDTO.class, name = "DefiniteGameDTO")
})
@JsonView(Views.Public.class)
public class CompetitionDTO extends ActivityDTO {

    private Requirement requirement;

    public Requirement getRequirement() {
        return requirement;
    }

    public CompetitionDTO(ActivityDTO activity, Requirement requirement) {
        super(activity);
        this.requirement = requirement;
    }

    public CompetitionDTO(CompetitionDTO activity) {
        this(activity, activity.getRequirement());
    }
}
