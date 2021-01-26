package at.rtr.rmbt.model.converter;

import at.rtr.rmbt.model.enums.ServerType;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter(autoApply = true)
public class ServerTypeConverter implements AttributeConverter<ServerType, String> {
    @Override
    public String convertToDatabaseColumn(ServerType attribute) {
        if (attribute != null)
        return attribute.getLabel();
        else
            return null;
    }

    @Override
    public ServerType convertToEntityAttribute(String dbData) {
        return ServerType.forValue(dbData);
    }
}