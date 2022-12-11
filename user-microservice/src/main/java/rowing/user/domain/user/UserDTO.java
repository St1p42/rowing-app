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

import javax.persistence.*;
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
        @JsonSubTypes.Type(value = UserDTO.class, name = "DefiniteGameDTO")
})
@JsonView(Views.Public.class)

public class UserDTO implements DTO {

    private UUID userId;

    private List<Position> rowingPositions;

    private List<AvailabilityIntervals> availability;

    private String email;

    private String firstName;

    private String lastName;

    private List<CoxCertificate> coxCertificates;

    private Gender gender;

    private String rowingOrganization;

    private Boolean competitive;

    public UUID getUserId() {
        return userId;
    }

    public List<Position> getRowingPositions() {
        return rowingPositions;
    }

    public List<AvailabilityIntervals> getAvailability() {
        return availability;
    }

    public String getEmail() {
        return email;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public List<CoxCertificate> getCoxCertificates() {
        return coxCertificates;
    }

    public Gender getGender() {
        return gender;
    }

    public String getRowingOrganization() {
        return rowingOrganization;
    }

    public Boolean getCompetitive() {
        return competitive;
    }

    public UserDTO(UserDTO user) {
        this.userId = user.getUserId();
        this.rowingPositions = user.getRowingPositions();
        this.availability = user.getAvailability();
        this.email = user.getEmail();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.coxCertificates = user.getCoxCertificates();
        this.gender = user.getGender();
        this.rowingOrganization = user.getRowingOrganization();
        this.competitive = user.getCompetitive();
    }
}


