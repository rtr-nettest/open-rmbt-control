package at.rtr.rmbt.model.converter;

import at.rtr.rmbt.enums.SpeedDirection;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter(autoApply = true)
public class SpeedDirectionConverter implements AttributeConverter<SpeedDirection, String> {

    @Override
    public String convertToDatabaseColumn(SpeedDirection attribute) {
        if (attribute != null) {
            return attribute.getValue();
        } else {
            return null;
        }
    }

    @Override
    public SpeedDirection convertToEntityAttribute(String dbData) {
        return SpeedDirection.forValue(dbData);
    }
}
