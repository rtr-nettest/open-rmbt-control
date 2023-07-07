package at.rtr.rmbt.model.converter;

import at.rtr.rmbt.enums.TestPlatform;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class TestPlatformConverter implements AttributeConverter<TestPlatform, String> {
    @Override
    public String convertToDatabaseColumn(TestPlatform attribute) {
        if (attribute != null)
            return attribute.getLabel();
        else
            return null;
    }

    @Override
    public TestPlatform convertToEntityAttribute(String dbData) {
        return TestPlatform.forValue(dbData);
    }
}
