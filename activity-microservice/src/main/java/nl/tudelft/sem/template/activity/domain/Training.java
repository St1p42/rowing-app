package nl.tudelft.sem.template.activity.domain;

import lombok.Data;

import javax.persistence.*;
import java.util.List;

@Data
@Entity
@Table(name = "activities")
public class Training extends Activity {
    @Id
    @Column(name = "activityName", nullable = false, unique = false)
    private String activityName;

    @Column(name = "participants", nullable = true, unique = false)
    @Enumerated(EnumType.STRING)
    @ElementCollection
    private List<Participant> participants;

    @Column(name = "owner", nullable = false, unique = false)
    private String activityOwner;

    @Column(name = "type", nullable = true, unique = false)
    private String type;
}
