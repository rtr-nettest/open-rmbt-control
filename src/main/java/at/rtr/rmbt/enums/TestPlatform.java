package at.rtr.rmbt.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;

import java.util.Objects;

@Getter
public enum TestPlatform {
    ANDROID("Android"),
    IOS("iOS"),

    WINDOWS("WINDOWS_NT"),
    DARWIN("DARWIN"),
    LINUX("LINUX");

    private String label;

    TestPlatform(String label) {
        this.label = label;
    }

    @JsonCreator
    public static TestPlatform forValue(String value) {
        if (Objects.isNull(value)) {
            return null;
        }
        try {
            return valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) { // if value not found by name - try find by label
            for (TestPlatform testPlatform : values()) {
                if (testPlatform.label.toUpperCase().equals(value.toUpperCase()))
                    return testPlatform;
            }
        }
        return null;
    }
}
