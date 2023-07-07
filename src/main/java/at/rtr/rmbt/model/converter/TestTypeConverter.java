package at.rtr.rmbt.model.converter;

import at.rtr.rmbt.enums.TestType;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class TestTypeConverter implements AttributeConverter<TestType, String> {

    @Override
    public String convertToDatabaseColumn(TestType attribute) {
        if (attribute != null) {
            return attribute.getValue();
        } else {
            return null;
        }
    }

    @Override
    public TestType convertToEntityAttribute(String dbData) {
        return TestType.forValue(dbData);
    }
}
