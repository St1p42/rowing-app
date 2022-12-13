package rowing.activity.domain;

import rowing.activity.domain.entities.Activity;
import rowing.commons.Position;

import java.util.*;

public interface Builder {

    void setId(UUID id);

    void setName(String name);

    void setType(String type);

    void setOwner(UUID owner);

    void setStart(Date start);

    void setPositions(List<Position> positions);

    Activity build();
}
