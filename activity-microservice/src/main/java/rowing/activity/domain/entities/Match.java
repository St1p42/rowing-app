package rowing.activity.domain.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.bytebuddy.implementation.bind.annotation.IgnoreForBinding;
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

    @Column(name = "userName", nullable = false, unique = true)
    private String userName;


    public Match (String userName, UUID activityId) {
        this.id = UUID.randomUUID();
        this.activityId = activityId;
        this.userName = userName;
    }
}
