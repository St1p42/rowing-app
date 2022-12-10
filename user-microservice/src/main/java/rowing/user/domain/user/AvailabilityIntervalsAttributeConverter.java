package rowing.user.domain.user;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.text.SimpleDateFormat;

@Converter
public class AvailabilityIntervalsAttributeConverter implements AttributeConverter<AvailabilityIntervals, String> {

    @Override
    public String convertToDatabaseColumn(AvailabilityIntervals attribute) {
        return attribute.toString();
    }

    @Override
    public AvailabilityIntervals convertToEntityAttribute(String dbData) {
        return new AvailabilityIntervals(dbData);
    }
}

