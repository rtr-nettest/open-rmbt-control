package at.rtr.rmbt.model.converter;

import at.rtr.rmbt.enums.QoeCategory;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class QoeCategoryConverter implements AttributeConverter<QoeCategory, String> {

    @Override
    public String convertToDatabaseColumn(QoeCategory attribute) {
        if (attribute != null) {
            return attribute.getValue();
        } else {
            return null;
        }
    }

    @Override
    public QoeCategory convertToEntityAttribute(String s) {
        return QoeCategory.forValue(s);
    }
}
