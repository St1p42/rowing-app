package rowing.activity.domain.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import rowing.activity.domain.utils.BaseEntity;
import rowing.commons.Position;
import rowing.commons.entities.ActivityDTO;
import org.modelmapper.ModelMapper;

import javax.persistence.*;
import java.time.Duration;
import java.util.*;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Inheritance(strategy = InheritanceType.JOINED)
@Table(indexes = {@Index(name = "id", columnList = "id")})
public abstract class Activity<T extends ActivityDTO> extends BaseEntity<T> {
    @Column(name = "id", nullable = false, unique = true)
    private UUID id;

    @Column(name = "owner", nullable = false, unique = false)
    private UUID owner;

    @Column(name = "name", nullable = false, unique = false)
    private String name;

    @Column(name = "type", nullable = true, unique = false)
    private String type;

    @Column(name = "date", nullable = false, unique = false)
    private Date start;

    @Column(name = "positions", nullable = true, unique = false)
    @ElementCollection
    private List<Position> positions;


    public Activity(ActivityDTO dto) {
        ModelMapper mapper = new ModelMapper();
        mapper.addConverter(
                context -> Duration.ofMillis(context.getSource()),
                Integer.class, Duration.class);
        mapper.getConfiguration().setSkipNullEnabled(true);
        if (dto.getId() == null) {
            // This id will change once the activity entity is saved, but it must be non-null
            this.id = UUID.randomUUID();
        } else {
            this.id = dto.getId();
        }
        this.name = dto.getName();
        this.owner = dto.getOwner();

        this.type = dto.getType();
    }

    protected ActivityDTO toDTO() {
        return new ActivityDTO(
                this.id,
                this.owner,
                this.name,
                this.type,
                this.start,
                this.positions
                );
    }

    public abstract T getDTO();
}
