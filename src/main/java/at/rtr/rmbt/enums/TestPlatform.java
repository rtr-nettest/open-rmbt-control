package at.rtr.rmbt.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

import static com.vladmihalcea.hibernate.type.util.LogUtils.LOGGER;

@Slf4j
@Getter
public enum TestPlatform {
    ANDROID("Android"),
    IOS("iOS"),
    CLI("CLI"),
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
                if (testPlatform.label.equalsIgnoreCase(value)) {
                    LOGGER.info(value, testPlatform);
                    return testPlatform;
                }
            }
        }
        return null;
    }
}
