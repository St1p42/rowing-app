package rowing.activity.domain;

import rowing.commons.Requirement;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

/**
 * JPA Converter for the Requirement value object.
 */
@Converter
public class RequirementAttributeConverter implements AttributeConverter<Requirement, String> {

    @Override
    public String convertToDatabaseColumn(Requirement attribute) {
        return attribute.toString();
    }

    @Override
    public Requirement convertToEntityAttribute(String dbData) {
        //To do
        return new Requirement();
    }

}
