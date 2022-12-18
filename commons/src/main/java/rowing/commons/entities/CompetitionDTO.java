package rowing.commons.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
@JsonView(Views.Public.class)
public class CompetitionDTO extends ActivityDTO {

    private Gender gender;
    private String organisation;

    /**
     * Constructor for CompetitionDTO.
     *
     * @param activity parent of the class
     *
     * @param gender required for the competition
     *
     * @param organisation required for the competition
     */
    public CompetitionDTO(ActivityDTO activity, Gender gender, String organisation) {
        super(activity);
        this.gender = gender;
        this.organisation = organisation;
    }

    /**
     * Getter for gender.
     *
     * @return gender required for the competition
     */
    public Gender getGender() {
        return gender;
    }

    /**
     * Getter for organisation.
     *
     * @return  organisation required for the competition.
     */
    public String getOrganisation() {
        return organisation;
    }

    /**
     * A constructor for CompetitionDTO.
     *
     * @param activity DTO that will be used
     */
    public CompetitionDTO(CompetitionDTO activity) {
        this(activity, activity.getGender(), activity.getOrganisation());
    }
}
