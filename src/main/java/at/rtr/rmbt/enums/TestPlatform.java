package at.rtr.rmbt.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;

@Getter
public enum TestPlatform {
    ANDROID("Android");

    private String label;

    TestPlatform(String label) {
        this.label = label;
    }

    @JsonCreator
    public static TestPlatform forValue(String value) {
        try {
            return valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) { // if value not found by name - try find by label
            for (TestPlatform testPlatform : values()) {
                if (testPlatform.label.equals(value))
                    return testPlatform;
            }
        }
        return null;
    }
}