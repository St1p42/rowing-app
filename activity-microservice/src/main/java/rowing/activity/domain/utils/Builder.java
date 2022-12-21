package rowing.activity.domain.utils;

import rowing.activity.domain.entities.Activity;
import rowing.commons.Position;

import java.util.*;

public interface Builder {

    void setId(UUID id);

    void setName(String name);

    void setType(String type);

    void setOwner(String owner);

    void setStart(Date start);

    void setLocation(String location);

    void setPositions(List<Position> positions);

    void setApplicants(List<String> applicants);

    Activity build();
}
