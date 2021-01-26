package at.rtr.rmbt.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;

@Getter
public enum ServerType {
    RMBT("RMBT"), RMBT_WS("RMBTws"), HW_PROBE("HW-PROBE"), RMBT_HTTP("RMBThttp");

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
