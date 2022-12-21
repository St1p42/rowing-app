package rowing.commons.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.*;
import rowing.commons.AvailabilityIntervals;
import rowing.commons.CoxCertificate;
import rowing.commons.Gender;
import rowing.commons.Position;
import rowing.commons.entities.utils.DTO;
import rowing.commons.entities.utils.Views;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@ToString(callSuper = true)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
@JsonSubTypes({
        @JsonSubTypes.Type(value = UpdateUserDTO.class, name = "UserDTO")
})
@JsonView(Views.Public.class)
@Data
@Getter
@Setter
public class UpdateUserDTO implements DTO {

    private List<Position> rowingPositions;

    private List<AvailabilityIntervals> availability;

    private String email;

    private String firstName;

    private String lastName;

    private List<String> coxCertificates;

    private Gender gender;

    private String rowingOrganization;

    private Boolean competitive;

}


