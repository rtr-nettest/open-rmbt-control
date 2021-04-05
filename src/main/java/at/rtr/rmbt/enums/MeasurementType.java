package at.rtr.rmbt.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;

import java.util.Objects;

@AllArgsConstructor
public enum MeasurementType {
    REGULAR("regular", "Regular"),
    DEDICATED("dedicated", "Dedicated"),
    LOOP_ACTIVE("loop_active", "Loop Active"),
    LOOP_WAITING("loop_waiting", "Loop Waiting");

    private final String value;
    private final String valueEn;

    @JsonValue
    public String getValue() {
        return value;
    }

    public String getValueEn() {
        return valueEn;
    }

    @JsonCreator
    public static MeasurementType forValue(String value) {
        if (Objects.isNull(value)) {
            return null;
        }
        for (MeasurementType measurementType : values()) {
            if (measurementType.value.equalsIgnoreCase(value))
                return measurementType;
        }
        throw new IllegalArgumentException();
    }
}
