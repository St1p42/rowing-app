package rowing.user.models;

import lombok.Data;

@Data
public class TwoAvailabilitiesModel {

    AvailabilityModel oldAvailability;
    AvailabilityModel newAvailability;

    public TwoAvailabilitiesModel(AvailabilityModel oldAvailability, AvailabilityModel newAvailability) {
        this.oldAvailability = oldAvailability;
        this.newAvailability = newAvailability;
    }
}
