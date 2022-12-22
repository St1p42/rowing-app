package rowing.commons.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.Data;
import lombok.ToString;
import rowing.commons.AvailabilityIntervals;
import rowing.commons.Gender;
import rowing.commons.Position;
import rowing.commons.entities.UserDTO;
import rowing.commons.entities.utils.Views;

import java.util.List;

@Data
@ToString(callSuper = true)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
@JsonView(Views.Public.class)
public class UserDTORequestModel extends UserDTO{

    private Position positionSelected;

    public UserDTORequestModel(){}

    public UserDTORequestModel(String userId, List<Position> rowingPositions,
                        List<AvailabilityIntervals> availability, String email,
                        String firstName, String lastName, List<String> coxCertificates,
                        Gender gender, String rowingOrganization, Boolean competitive, Position selected) {

        super(userId, rowingPositions, availability, email, firstName, lastName, coxCertificates,
                gender, rowingOrganization, competitive);

        positionSelected = selected;
    }

    public UserDTORequestModel(UserDTO userDTO, Position selected) {

        super(userDTO.getUserId(), userDTO.getRowingPositions(), userDTO.getAvailability(),
                userDTO.getEmail(), userDTO.getFirstName(), userDTO.getLastName(), userDTO.getCoxCertificates(),
                userDTO.getGender(), userDTO.getRowingOrganization(), userDTO.getCompetitive());

        positionSelected = selected;
    }
}
