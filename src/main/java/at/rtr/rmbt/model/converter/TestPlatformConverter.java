package at.rtr.rmbt.model.converter;

import at.rtr.rmbt.enums.TestPlatform;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

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
        return dbData == null ? TestPlatform.ANDROID : TestPlatform.forValue(dbData);
    }
}
