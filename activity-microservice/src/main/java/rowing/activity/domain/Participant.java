package rowing.activity.domain;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
public class Participant {
    @Id
    @Column(name = "id", nullable = false)
    private Long id;

}
