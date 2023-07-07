package at.rtr.rmbt.model.converter;

import at.rtr.rmbt.enums.NetworkGroupName;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

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
