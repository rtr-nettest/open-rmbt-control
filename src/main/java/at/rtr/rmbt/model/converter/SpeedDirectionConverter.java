package at.rtr.rmbt.model.converter;

import at.rtr.rmbt.enums.SpeedDirection;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * Speed direction converter class.
 */
@Converter(autoApply = true)
public class SpeedDirectionConverter implements AttributeConverter<SpeedDirection, String> {

    /**
     * Convert to database column.
     *
     * @param attribute the Attribute
     * @return the result
     */
    @Override
    public String convertToDatabaseColumn(SpeedDirection attribute) {
        if (attribute != null) {
            return attribute.getValue();
        } else {
            return null;
        }
    }

    /**
     * Convert to entity attribute.
     *
     * @param dbData the Db data
     * @return the result
     */
    @Override
    public SpeedDirection convertToEntityAttribute(String dbData) {
        return SpeedDirection.forValue(dbData);
    }
}
