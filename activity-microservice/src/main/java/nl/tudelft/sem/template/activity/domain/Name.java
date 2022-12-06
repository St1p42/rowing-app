package nl.tudelft.sem.template.activity.domain;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode
public class Name {
    private final String name;

    public Name(String name) {
        // validate NetID
        this.name = name;
    }

    @Override
    public String toString() {
        return this.name;
    }
}
