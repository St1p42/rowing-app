package rowing.commons.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import rowing.commons.Gender;
import rowing.commons.entities.utils.Views;

@AllArgsConstructor
@Data
@NoArgsConstructor
@ToString(callSuper = true)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
@JsonSubTypes({
        @JsonSubTypes.Type(value = CompetitionDTO.class, name = "CompetitionDTO")
})
@JsonView(Views.Public.class)
public class CompetitionDTO extends ActivityDTO {

    private Gender gender;
    private String organisation;

    public CompetitionDTO(ActivityDTO activity, Gender gender, String organisation) {
        super(activity);
        this.gender = gender;
        this.organisation = organisation;
    }

    public Gender getGender() {
        return gender;
    }

    public String getOrganisation() {
        return organisation;
    }

    public CompetitionDTO(CompetitionDTO activity) {
        this(activity, activity.getGender(), activity.getOrganisation());
    }
}
