package at.rtr.rmbt.model.converter;

import at.rtr.rmbt.enums.ServerType;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * Server type converter class.
 */
@Converter(autoApply = true)
public class ServerTypeConverter implements AttributeConverter<ServerType, String> {
    /**
     * Convert to database column.
     *
     * @param attribute the Attribute
     * @return the result
     */
    @Override
    public String convertToDatabaseColumn(ServerType attribute) {
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
    public ServerType convertToEntityAttribute(String dbData) {
        return ServerType.forValue(dbData);
    }
}