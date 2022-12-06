package nl.tudelft.sem.template.activity.domain;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;


@Data
public abstract class Activity {
    private Name name;
    private Owner owner;
    private List<Participant> participants;

}
