package at.rtr.rmbt.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;

import java.util.Objects;

@Getter
public enum NetworkGroupName { //todo update to not fail on input
    G2("2G", "GPRS (2G)"),
    G4("4G", "LTE (4G)"),
    G2_G4("2G/4G", "GPRS (2G) + LTE (4G)"),
    LAN("LAN", "LAN"),
    G2_G3("2G/3G", "GPRS (2G) + UMTS (3G)"),
    WLAN("WLAN", "WLAN"),
    G2_G3_G4("2G/3G/4G", "GPRS (2G) + UMTS (3G) + LTE (4G)"),
    G3("3G", "UMTS (3G)"),
    G5("5G", "NRNSA (5G)"),
    G3_G4("3G/4G", "UMTS (3G) + LTE (4G)"),
    UNKNOWN("", "Unknown Network");

    private String label;
    private String labelEn;

    NetworkGroupName(String label, String labelEn) {
        this.label = label;
        this.labelEn = labelEn;
    }

    @JsonCreator
    public static NetworkGroupName forValue(String value) {
        if (Objects.isNull(value)) {
            return null;
        }
        for (NetworkGroupName networkGroupName : values()) {
            if (networkGroupName.label.equals(value))
                return networkGroupName;
        }
        return UNKNOWN;
    }
}
