package at.rtr.rmbt.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Objects;

public enum SpeedDirection {

    DOWNLOAD("download"),
    UPLOAD("upload");

    SpeedDirection(String value) {
        this.value = value;
    }

    private final String value;


    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static SpeedDirection forValue(String value) {
        if (Objects.isNull(value)) {
            return null;
        }
        for (SpeedDirection speedDirection : values()) {
            if (speedDirection.value.equals(value))
                return speedDirection;
        }
        throw new IllegalArgumentException();
    }
}
