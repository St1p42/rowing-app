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
@Table(name = "matching")
public class Match {
    @Id
    private UUID id;

    @Column
    private UUID activityId;

    @Column(name = "userId", nullable = false, unique = true)
    private UUID userId;

    @Column(name = "position", nullable = false, unique = true)
    private Position position;

}
