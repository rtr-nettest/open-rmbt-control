package at.rtr.rmbt.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ASInformation {

    private final String name;
    private final String country;
    private final Long number;

    public String getCountry() {
        if (country != null && country.length() > 2) {
            return country.substring(0, 2);
        }
        return country;
    }
}
