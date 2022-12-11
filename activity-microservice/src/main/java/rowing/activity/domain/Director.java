package rowing.activity.domain;

import rowing.commons.entities.ActivityDTO;
import rowing.commons.entities.CompetitionDTO;

public class Director {
    public void constructTraining(TrainingBuilder builder, ActivityDTO dto) {
        builder.setId(dto.getId());
        builder.setName(dto.getName());
        builder.setOwner(dto.getOwner());
        builder.setType("T");
        builder.setStart(dto.getStart());
        builder.setPositions(dto.getPositions());
    }

    public void constructCompetition(CompetitionBuilder builder, CompetitionDTO dto) {
        builder.setId(dto.getId());
        builder.setName(dto.getName());
        builder.setOwner(dto.getOwner());
        builder.setType("T");
        builder.setStart(dto.getStart());
        builder.setPositions(dto.getPositions());
        builder.setRequirement(dto.getRequirement());
    }
}
