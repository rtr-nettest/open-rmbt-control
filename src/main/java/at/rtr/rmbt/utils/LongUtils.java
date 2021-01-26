package at.rtr.rmbt.utils;

import lombok.experimental.UtilityClass;

@UtilityClass
public class LongUtils {

    public Long parseLong(String s) {
        Long number = null;
        try {
            number = Long.valueOf(s);
        } catch (NumberFormatException e) {
        }
        return number;
    }
}
