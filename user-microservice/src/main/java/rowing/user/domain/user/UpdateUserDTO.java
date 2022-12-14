package rowing.user.domain.user;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import rowing.user.domain.user.utils.DTO;
import rowing.user.domain.user.utils.Views;

import java.util.List;

@AllArgsConstructor
@Data
@NoArgsConstructor
@ToString(callSuper = true)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
@JsonSubTypes({
        @JsonSubTypes.Type(value = UpdateUserDTO.class, name = "UserDTO")
})
@JsonView(Views.Public.class)

public class UpdateUserDTO implements DTO {

    private List<Position> rowingPositions;

    private List<AvailabilityIntervals> availability;

    private String email;

    private String firstName;

    private String lastName;

    private List<CoxCertificate> coxCertificates;

    private Gender gender;

    private String rowingOrganization;

    private Boolean competitive;

}


