package at.rtr.rmbt.model.converter;

import at.rtr.rmbt.enums.NetworkGroupName;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * Network group name converter class.
 */
@Converter(autoApply = true)
public class NetworkGroupNameConverter implements AttributeConverter<NetworkGroupName, String> {
    /**
     * Convert to database column.
     *
     * @param attribute the Attribute
     * @return the result
     */
    @Override
    public String convertToDatabaseColumn(NetworkGroupName attribute) {
        if (attribute != null)
            return attribute.getLabel();
        else
            return null;
    }

    /**
     * Convert to entity attribute.
     *
     * @param dbData the Db data
     * @return the result
     */
    @Override
    public NetworkGroupName convertToEntityAttribute(String dbData) {
        return NetworkGroupName.forValue(dbData);
    }
}
