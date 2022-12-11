package rowing.commons.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.*;
import rowing.commons.Position;
import rowing.commons.entities.utils.DTO;
import rowing.commons.entities.utils.Views;

import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Dto for any activity
 */
@Data
@NoArgsConstructor
@ToString(callSuper = true)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
@JsonSubTypes({
        @JsonSubTypes.Type(value = ActivityDTO.class, name = "ActivityDTO")
})
@JsonView(Views.Public.class)
public class ActivityDTO implements DTO {

    private UUID id;
    private UUID owner;
    private String type;
    private String name;
    private Date start;
    private List<Position> positions;

    /**
     * Getter for the id
     * @return
     */
    public UUID getId() {
        return id;
    }

    public UUID getOwner() {
        return owner;
    }

    public String getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public ActivityDTO(UUID id, UUID owner, String type, String name, Date start, List<Position> positions) {
        this.id = id;
        this.owner = owner;
        this.type = type;
        this.name = name;
        this.start = start;
        this.positions = positions;
    }

    public Date getStart() {
        return start;
    }

    public List<Position> getPositions() {
        return positions;
    }

    public ActivityDTO(ActivityDTO dto) {
        this.id = dto.getId();
        this.owner = dto.getOwner();
        this.name = dto.getName();
        this.type = dto.getType();
        this.start = dto.getStart();
        this.positions = dto.getPositions();
    }
}
