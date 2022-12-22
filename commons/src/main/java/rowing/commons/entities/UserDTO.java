package rowing.commons.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import rowing.commons.AvailabilityIntervals;
import rowing.commons.CoxCertificate;
import rowing.commons.Gender;
import rowing.commons.Position;
import rowing.commons.entities.utils.DTO;
import rowing.commons.entities.utils.Views;

import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@Data
@NoArgsConstructor
@JsonView(Views.Public.class)
public class UserDTO implements DTO {

    private String userId;

    private List<Position> rowingPositions;

    private List<AvailabilityIntervals> availability;

    private String email;

    private String firstName;

    private String lastName;

    private List<String> coxCertificates;

    private Gender gender;

    private String rowingOrganization;

    private Boolean competitive;

    /**
     * Construct a UserDTO.
     *
     * @param rowingPositions - positions
     * @param availability - availability
     * @param email - email
     * @param firstName - first name
     * @param lastName - last name
     * @param coxCertificates - certificates
     * @param gender - gender
     * @param rowingOrganization - organization
     * @param competitive - status of competition
     */
    public UserDTO(List<Position> rowingPositions, List<AvailabilityIntervals> availability, String email,
                   String firstName, String lastName, List<String> coxCertificates, Gender gender,
                   String rowingOrganization, Boolean competitive) {
        this.rowingPositions = rowingPositions;
        this.availability = availability;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.coxCertificates = coxCertificates;
        this.gender = gender;
        this.rowingOrganization = rowingOrganization;
        this.competitive = competitive;
    }
}


