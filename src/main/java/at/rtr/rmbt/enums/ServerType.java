package at.rtr.rmbt.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;

@Getter
public enum ServerType {
    RMBT("RMBT"), RMBTws("RMBTws"), HW_PROBE("HW-PROBE"), RMBThttp("RMBThttp"), QoS("QoS"), RMBTel("RMBTel");

    private String label;

    ServerType(String label) {
        this.label = label;
    }

    @JsonCreator
    public static ServerType forValue(String value) {
        for (ServerType serverType : values()) {
            if (serverType.label.equals(value))
                return serverType;
        }
        return null;
    }
}
