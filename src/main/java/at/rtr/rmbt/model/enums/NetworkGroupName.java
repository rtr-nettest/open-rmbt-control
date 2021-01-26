package at.rtr.rmbt.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;

@Getter
public enum NetworkGroupName {
    G2("2G"),
    G4("4G"),
    G2_G4("2G/4G"),
    LAN("LAN"),
    G2_G3("2G/3G"),
    WLAN("WLAN"),
    G2_G3_G4("2G/3G/4G"),
    G3("3G");

    private String label;

    NetworkGroupName(String label) {
        this.label = label;
    }

    @JsonCreator
    public static NetworkGroupName forValue(String value) {
        for (NetworkGroupName networkGroupName : values()) {
            if (networkGroupName.label.equals(value))
                return networkGroupName;
        }
        return null;
    }
}
