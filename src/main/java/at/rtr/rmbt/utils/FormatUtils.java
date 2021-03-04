package at.rtr.rmbt.utils;

import lombok.experimental.UtilityClass;

import java.util.Optional;

@UtilityClass
public class FormatUtils {

    public static String format(String template, Long value) {
        return Optional.ofNullable(value)
                .map(x -> String.format(template, x))
                .orElse("");
    }
}
