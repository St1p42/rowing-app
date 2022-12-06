package nl.tudelft.sem.template.notification.domain.notification;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Objects;

/**
 * Notification entity to store in the database.
 * In constructor takes all the arguments listed as fields including the reader object
 * which gives different toString() results based on the notification status.
 */
@Entity
@Table(name = "notifications")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Notification {
    @Id
    @Column(name = "notificationId", nullable = false, unique = true)
    private int id;

    @Column(name = "activity", nullable = false, unique = false)
    private int activityId;

    @Column(name = "user", nullable = false, unique = false)
    private int userId;


    @Column(name = "notificationStatus", nullable = true, unique = false)
    @Enumerated(EnumType.STRING)
    private NotificationStatus status;

    //field is ignored when the notification is added to the database
    //but not ignored when converting to JSON
    @JsonInclude()
    @Transient
    private NotificationReader reader;


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
        Notification other = (Notification) o;
        return this.id == (other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId);
    }

    /**
     * Converts the notification to the text message.
     * @return String representing the message for the user.
     */
    @Override
    public String toString() {
        return this.reader.read();
    }
}
