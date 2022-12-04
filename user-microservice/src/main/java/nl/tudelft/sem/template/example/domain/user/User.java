package nl.tudelft.sem.template.example.domain.user;

import java.util.List;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nl.tudelft.sem.template.example.domain.HasEvents;

/**
 * A DDD entity representing an application user in our domain.
 */
@Entity
@Table(name = "users")
@NoArgsConstructor
@Getter
@Setter
public class User extends HasEvents {
    /**
     * Identifier for the application user.
     */
    @Id
    @Column(name = "userId", nullable = false, unique = true)
    private int userId;

    @Column(name = "rowingPositions", nullable = true, unique = false)
    @Enumerated(EnumType.STRING)
    @ElementCollection
    private List<Position> rowingPositions;

    @Column(name = "availability", nullable = true, unique = false)
    @Convert(converter = AvailabilityIntervalsAttributeConverter.class)
    private List<AvailabilityIntervals> availability;

    @Column(name = "firstName", nullable = false, unique = false)
    private String firstName;

    @Column(name = "lastName", nullable = false, unique = false)
    private String lastName;

    @Column(name = "coxCertificates", nullable = true, unique = false)
    @Enumerated(EnumType.STRING)
    @ElementCollection
    private List<CoxCertificate> coxCertificates;

    @Column(name = "gender", nullable = false, unique = false)
    private Gender gender;

    @Column(name = "rowingOrganization", nullable = true, unique = false)
    private String rowingOrganization;

    @Column(name = "competitive", nullable = true, unique = false)
    private Boolean competitive;

    /**
     * Creates user with must fill attributes.
     *
     * @param userId - the unique identifier of the user
     * @param firstName - the first name of the user
     * @param lastName - the last name of the user
     */
    public User(int userId, String firstName, String lastName) {
        //TODO validation if necessary
        this.userId = userId;
        this.firstName = firstName;
        this.lastName = lastName;
        //this.recordThat(new UserWasCreatedEvent(netId));
    }

    /**
     * Craete attributes for basic profile.
     *
     * @param rowingPositions - positions allowed to fill
     * @param availability - availability schedule
     * @param coxCertificates - certificates
     */
    public void createProfileBasic(List<Position> rowingPositions, List<AvailabilityIntervals> availability,
                                   List<CoxCertificate> coxCertificates) {
        //TODO validation if necessary
        this.rowingPositions = rowingPositions;
        this.availability = availability;
        this.coxCertificates = coxCertificates;
    }

    /**
     * Create attributes for competition for a user.
     *
     * @param gender - gender of user
     * @param rowingOrganization - organization of user
     * @param competitive - if he wants to participate competitively
     */
    public void createProfileCompetitive(Gender gender, String rowingOrganization, boolean competitive) {
        //TODO validation if necessary
        this.gender = gender;
        this.rowingOrganization = rowingOrganization;
        this.competitive = competitive;
    }

    /**
     * Equality is only based on the identifier.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        User appUser = (User) o;
        return userId == (appUser.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId);
    }
}
