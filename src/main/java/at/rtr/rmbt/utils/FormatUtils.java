package at.rtr.rmbt.utils;

import at.rtr.rmbt.constant.Config;
import at.rtr.rmbt.constant.Constants;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.text.Format;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;

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

    public static String formatValueAndUnit(Double value, String unitValue, Locale locale) {
        Format format = NumberFormat.getNumberInstance(locale);
        MathContext mathContext = new MathContext(Config.SIGNIFICANT_PLACES, RoundingMode.HALF_UP);
        return String.format(Constants.VALUE_AND_UNIT_TEMPLATE, format.format(new BigDecimal(value, mathContext)), unitValue);
    }

    public static String formatValueAndUnit(Long value, String unitValue) {
        return String.format(Constants.VALUE_AND_UNIT_TEMPLATE, value, unitValue);
    }

    public static String formatValueAndUnit(Integer value, String unitValue) {
        return String.format(Constants.VALUE_AND_UNIT_TEMPLATE, value, unitValue);
    }

    public static String formatSpeed(Integer speed) {
        return Optional.ofNullable(speed)
                .map(s -> ObjectUtils.defaultIfNull(s, NumberUtils.INTEGER_ZERO))
                .map(x -> x / Constants.BYTES_UNIT_CONVERSION_MULTIPLICATOR)
                .map(numberFormat::format)
                .orElse(StringUtils.EMPTY);
    }

    public static String formatPing(Long ping) {
        return Optional.ofNullable(ping)
                .map(s -> ObjectUtils.defaultIfNull(s, NumberUtils.LONG_ZERO))
                .map(x -> x / Constants.PING_CONVERSION_MULTIPLICATOR)
                .map(numberFormat::format)
                .orElse(StringUtils.EMPTY);
    }

    public static String formatLoopUUID(UUID loopUUID) {
        return Optional.ofNullable(loopUUID)
                .map(uuid -> String.format(Constants.TEST_HISTORY_LOOP_UUID_TEMPLATE, uuid))
                .orElse(null);
    }
}
