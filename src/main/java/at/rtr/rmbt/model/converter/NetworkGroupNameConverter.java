package at.rtr.rmbt.model.converter;

import at.rtr.rmbt.model.enums.NetworkGroupName;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter(autoApply = true)
public class NetworkGroupNameConverter implements AttributeConverter<NetworkGroupName, String> {
    @Override
    public String convertToDatabaseColumn(NetworkGroupName attribute) {
        if (attribute != null)
            return attribute.getLabel();
        else
            return null;
    }

    @Override
    public NetworkGroupName convertToEntityAttribute(String dbData) {
        return NetworkGroupName.forValue(dbData);
    }
}
