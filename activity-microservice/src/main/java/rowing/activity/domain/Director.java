package rowing.activity.domain;

import rowing.commons.entities.ActivityDTO;
import rowing.commons.entities.CompetitionDTO;

public class Director {
    /**
     * Function that will set training activity settings in order to build.
     *
     * @param builder that will build the activity
     * @param dto the dto that contains the information regarding activity parameters
     */
    public void constructTraining(TrainingBuilder builder, ActivityDTO dto) {
        builder.setId(dto.getId());
        builder.setName(dto.getName());
        builder.setOwner(dto.getOwner());
        builder.setType("Training");
        builder.setStart(dto.getStart());
        builder.setPositions(dto.getPositions());
        builder.setApplicants(dto.getApplicants());
    }

    /**
     * Function that will set competition activity settings in order to build.
     *
     * @param builder that will build the activity
     * @param dto the dto that contains the information regarding activity parameters
     */
    public void constructCompetition(CompetitionBuilder builder, CompetitionDTO dto) {
        builder.setId(dto.getId());
        builder.setName(dto.getName());
        builder.setOwner(dto.getOwner());
        builder.setType("Competition");
        builder.setStart(dto.getStart());
        builder.setPositions(dto.getPositions());
        builder.setGender(dto.getGender());
        builder.setOrganisation(dto.getOrganisation());
        builder.setApplicants(builder.getApplicants());
    }
}
