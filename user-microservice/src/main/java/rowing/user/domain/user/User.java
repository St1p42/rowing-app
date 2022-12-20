package rowing.user.domain.user;

import lombok.Data;
import lombok.NoArgsConstructor;
import rowing.commons.AvailabilityIntervals;
import rowing.commons.CoxCertificate;
import rowing.commons.Gender;
import rowing.commons.Position;
import rowing.user.domain.HasEvents;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * A DDD entity representing an application user in our domain.
 */
@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
public class User extends HasEvents {
    /**
     * Identifier for the application user.
     */
    @Id
    @Column(name = "userId", nullable = false, unique = true)
    private String userId;

    @Column(name = "rowingPositions")
    @Enumerated(EnumType.STRING)
    @ElementCollection
    private List<Position> rowingPositions;

    @Column(name = "availability")
    @Convert(converter = AvailabilityIntervalsAttributeConverter.class)
    private List<AvailabilityIntervals> availability = new ArrayList<>();

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "firstName", nullable = false)
    private String firstName;

    @Column(name = "lastName", nullable = false)
    private String lastName;

    @Column(name = "coxCertificates")
    @Enumerated(EnumType.STRING)
    @ElementCollection
    private List<CoxCertificate> coxCertificates;

    @Column(name = "gender", nullable = true, unique = false)
    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Column(name = "rowingOrganization")
    private String rowingOrganization;

    @Column(name = "competitive")
    private Boolean competitive;

    /**
     * Creates user with must fill attributes.
     *
     * @param userId - the unique identifier of the user
     * @param firstName - the first name of the user
     * @param lastName - the last name of the user
     * @param email - the email of the user to send notifications to
     */
    public User(String userId, String firstName, String lastName, String email) {
        //TODO validation if necessary
        this.userId = userId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        //this.recordThat(new UserWasCreatedEvent(username));
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
        return userId.equals(appUser.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId);
    }
}
