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
public class UserDTORequestModel extends UserDTO {

    private Position positionSelected;

    public UserDTORequestModel(){}

    /**
     * Constructor for request model of user DTO.
     *
     * @param userId - id of a user
     * @param rowingPositions - positions of a user
     * @param availability - availability of a user
     * @param email - user's email
     * @param firstName - user's first name
     * @param lastName - user's last name
     * @param coxCertificates - user's cox certificates
     * @param gender - user's gender
     * @param rowingOrganization - user's rowing organization
     * @param competitive - boolean, true if user is looking for competition, false if for a training session
     * @param selected - user's selected position for the activity
     */
    public UserDTORequestModel(String userId, List<Position> rowingPositions,
                        List<AvailabilityIntervals> availability, String email,
                        String firstName, String lastName, List<String> coxCertificates,
                        Gender gender, String rowingOrganization, Boolean competitive, Position selected) {

        super(userId, rowingPositions, availability, email, firstName, lastName, coxCertificates,
                gender, rowingOrganization, competitive);

        positionSelected = selected;
    }

    /**
     * Constructor for request model of User DTO using User DTO as a parameter.
     *
     * @param userDTO - DTO to create a request model from
     * @param selected - selected position of a user for the activity
     */
    public UserDTORequestModel(UserDTO userDTO, Position selected) {

        super(userDTO.getUserId(), userDTO.getRowingPositions(), userDTO.getAvailability(),
                userDTO.getEmail(), userDTO.getFirstName(), userDTO.getLastName(), userDTO.getCoxCertificates(),
                userDTO.getGender(), userDTO.getRowingOrganization(), userDTO.getCompetitive());

        positionSelected = selected;
    }
}
