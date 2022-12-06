package nl.tudelft.sem.template.activity.domain;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter
public class ActivityAttributeConverter implements AttributeConverter<Activity, String> {

    @Override
    public String convertToDatabaseColumn(Activity attribute) {
        //TODO implement these correctly
        return attribute.toString();
    }

    @Override
    public Activity convertToEntityAttribute(String dbData) {
        //TODO implement these correctly
        return new Training();
    }
}