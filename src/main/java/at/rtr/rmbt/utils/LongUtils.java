package at.rtr.rmbt.utils;

import lombok.experimental.UtilityClass;

/**
 * Long utils class.
 */
@UtilityClass
public class LongUtils {

    /**
     * Parse long.
     *
     * @param s the S
     * @return the result
     */
    public Long parseLong(String s) {
        Long number = null;
        try {
            number = Long.valueOf(s);
        } catch (NumberFormatException e) {
        }
        return number;
    }
}
