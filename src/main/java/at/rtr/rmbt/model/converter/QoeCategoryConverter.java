package at.rtr.rmbt.model.converter;

import at.rtr.rmbt.enums.QoeCategory;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * Qoe category converter class.
 */
@Converter(autoApply = true)
public class QoeCategoryConverter implements AttributeConverter<QoeCategory, String> {

    /**
     * Convert to database column.
     *
     * @param attribute the Attribute
     * @return the result
     */
    @Override
    public String convertToDatabaseColumn(QoeCategory attribute) {
        if (attribute != null) {
            return attribute.getValue();
        } else {
            return null;
        }
    }

    /**
     * Convert to entity attribute.
     *
     * @param s the S
     * @return the result
     */
    @Override
    public QoeCategory convertToEntityAttribute(String s) {
        return QoeCategory.forValue(s);
    }
}
