package at.rtr.rmbt.utils;

import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;

import java.text.NumberFormat;
import java.util.Locale;
import java.util.Optional;

@UtilityClass
public class FormatUtils {
    public static NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.US);

    public static String format(String template, Integer value) {
        return Optional.ofNullable(value)
                .map(x -> String.format(template, x))
                .orElse(StringUtils.EMPTY);
    }

    public static String format(String template, Double value) {
        numberFormat.setMaximumFractionDigits(2);
        return Optional.ofNullable(value)
                .map(x -> numberFormat.format(x))
                .map(x -> String.format(template, x))
                .orElse(StringUtils.EMPTY);
    }
}
