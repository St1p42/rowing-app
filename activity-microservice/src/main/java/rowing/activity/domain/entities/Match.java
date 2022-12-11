package rowing.activity.domain.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import rowing.commons.Position;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "activity")
public class Match {
    @EmbeddedId
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "activityId")
    private UUID activityId;

    @ManyToMany
    @Column(name = "userId", nullable = false, unique = true)
    private UUID userId;

    @Column(name = "position", nullable = false, unique = true)
    private Position position;

}
