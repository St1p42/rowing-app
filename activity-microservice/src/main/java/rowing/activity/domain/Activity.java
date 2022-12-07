package rowing.activity.domain;

import lombok.Data;

import javax.persistence.*;
import java.util.List;

@Data
@Entity
@Table(name = "activities")
public class Activity  {
    @Id
    @Column(name = "activityId", nullable = false, unique = true)
    private String activityId;

    @Column(name = "activityName", nullable = false, unique = false)
    private String activityName;

    @Column(name = "activityOwner", nullable = false, unique = false)
    private String activityOwner;

    @Column(name = "requirements", nullable = true, unique = false)
    @Convert(converter = RequirementAttributeConverter.class)
    private List<Requirement> requirements;

    @Column(name = "participants", nullable = true, unique = false)
    @ElementCollection
    private List<String> participantsUsername;

    @Column(name = "type", nullable = true, unique = false)
    private String type;
}
