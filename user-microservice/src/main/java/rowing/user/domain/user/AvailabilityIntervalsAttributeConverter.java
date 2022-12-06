package rowing.user.domain.user;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter
public class AvailabilityIntervalsAttributeConverter implements AttributeConverter<AvailabilityIntervals, String> {

    @Override
    public String convertToDatabaseColumn(AvailabilityIntervals attribute) {
        //TODO implement these correctly
        return attribute.toString();
    }

    @Override
    public AvailabilityIntervals convertToEntityAttribute(String dbData) {
        //TODO implement these correctly
        return new AvailabilityIntervals();
    }
}

