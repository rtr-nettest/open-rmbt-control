package at.rtr.rmbt.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;

/**
 * Server type enum.
 */
@Getter
public enum ServerType {
    RMBT("RMBT"),
    RMBTws("RMBTws"),
    RMBThttp("RMBThttp"),
    QoS("QoS"),
    RMBTudp("RMBTudp");

    private String label;

    /**
     * Creates a new ServerType instance.
     *
     * @param label the Label
     */
    ServerType(String label) {
        this.label = label;
    }

    /**
     * For value.
     *
     * @param value the Value
     * @return the result
     */
    @JsonCreator
    public static ServerType forValue(String value) {
        for (ServerType serverType : values()) {
            if (serverType.label.equals(value))
                return serverType;
        }
        return null;
    }
}
