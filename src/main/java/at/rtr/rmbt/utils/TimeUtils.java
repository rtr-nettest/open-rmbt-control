package at.rtr.rmbt.utils;

import at.rtr.rmbt.model.Test;
import lombok.experimental.UtilityClass;

import java.text.DateFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.chrono.ChronoZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Locale;
import java.util.Optional;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

@UtilityClass
public class TimeUtils {

    public Double getDiffInSecondsFromTwoZonedDateTime(ZonedDateTime first, ZonedDateTime second) {
        return ChronoUnit.MILLIS.between(first, second) / 1000.0;
    }

    public ZonedDateTime getZonedDateTimeFromMillisAndTimezone(Long millies, String timezone) {
        return ZonedDateTime.ofInstant(Instant.ofEpochMilli(millies), ZoneId.of(timezone));
    }

    public String getTimeStringFromTest(Test test, Locale locale) {
        Date date = Optional.ofNullable(test.getTime())
                .map(ChronoZonedDateTime::toInstant)
                .map(Date::from)
                .orElse(null);
        TimeZone timeZone = TimeZone.getTimeZone(test.getTimezone());
        return getTimeString(date, timeZone, locale);
    }

    public String getTimeString(Date date, TimeZone timeZone, Locale locale) {
        if (date == null)
            return null;
        final DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM, locale);
        dateFormat.setTimeZone(timeZone);
        return dateFormat.format(date);
    }

    public Double formatToSeconds(Long timeNs) {
        return timeNs != null && timeNs > 0 ? Math.round(timeNs / 1000000.0) / 1000.0 : 0.000;
    }

    public Long formatToSecondsRound(Long timeNs) {
        String.format("%d min, %d sec",
                TimeUnit.MILLISECONDS.toMinutes(timeNs),
                TimeUnit.MILLISECONDS.toSeconds(timeNs) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(timeNs)));
        return timeNs != null && timeNs > 0 ? TimeUnit.NANOSECONDS.toSeconds(timeNs) : 0;
    }
}
