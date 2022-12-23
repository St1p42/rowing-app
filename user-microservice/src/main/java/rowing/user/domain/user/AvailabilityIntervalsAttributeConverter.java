package rowing.user.domain.user;

import rowing.commons.AvailabilityIntervals;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.ArrayList;
import java.util.List;

@Converter
public class AvailabilityIntervalsAttributeConverter implements AttributeConverter<List<AvailabilityIntervals>, String> {

    @Override
    public String convertToDatabaseColumn(List<AvailabilityIntervals> attribute) {
        if (attribute == null) {
            return null;
        }
        return attribute.toString().substring(1, attribute.toString().length() - 1);
    }

    @Override
    public List<AvailabilityIntervals> convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.length() == 0) {
            return null;
        }
        System.out.println(dbData.length());
        String[] av = dbData.split(", ");
        List<AvailabilityIntervals> sol = new ArrayList<AvailabilityIntervals>();
        for (String availability : av) {
            System.out.println(availability);
            sol.add(new AvailabilityIntervals(availability));
        }
        return sol;
    }
}

