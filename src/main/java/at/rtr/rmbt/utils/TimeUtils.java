package at.rtr.rmbt.utils;

import lombok.experimental.UtilityClass;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;

@UtilityClass
public class TimeUtils {

    public static Double getDiffInSecondsFromTwoZonedDateTime(ZonedDateTime first, ZonedDateTime second) {
        return ChronoUnit.MILLIS.between(first, second) / 1000.0;
    }

    public static ZonedDateTime getZonedDateTimeFromMillisAndTimezone(Long millies, String timezone) {
        return ZonedDateTime.ofInstant(Instant.ofEpochMilli(millies), ZoneId.of(timezone));
    }
}
