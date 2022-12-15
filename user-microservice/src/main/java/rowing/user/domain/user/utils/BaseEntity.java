package rowing.user.domain.user.utils;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import java.util.UUID;

/**
 * The base class for all database entities.
 *
 */
@Data
@MappedSuperclass
@NoArgsConstructor
public abstract class BaseEntity<D extends DTO> {
    /**
     * id - random unique uuid assigned to a certain entity.
     */
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id", updatable = false, nullable = false)
    protected UUID id;

    /**
     * Convert the entity to a DTO.
    */
    public abstract D getDto();
}
