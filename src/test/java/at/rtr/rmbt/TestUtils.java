package at.rtr.rmbt;

import com.bedatadriven.jackson.datatype.jts.JtsModule;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.experimental.UtilityClass;

import static org.junit.Assert.assertEquals;

@UtilityClass
public class TestUtils {

    /** Narrow no-break space (U+202F) emitted before AM/PM by JDK 20+ CLDR data. */
    private static final char NARROW_NO_BREAK_SPACE = (char) 0x202F;
    /** No-break space (U+00A0), used as a defensive normalization target as well. */
    private static final char NO_BREAK_SPACE = (char) 0x00A0;

    public static final ObjectMapper mapper = new ObjectMapper()
        .configure(JsonGenerator.Feature.IGNORE_UNKNOWN, true)
        .configure(JsonParser.Feature.IGNORE_UNDEFINED, true)
        .setSerializationInclusion(JsonInclude.Include.NON_NULL)
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        .registerModule(new JavaTimeModule());

    public static String asJsonString(final Object obj) throws JsonProcessingException {
        mapper.registerModule(new JtsModule());
        return mapper.writeValueAsString(obj);
    }

    /**
     * Replaces the narrow no-break space (U+202F) and no-break space (U+00A0) with an ordinary space.
     * Newer JDK/CLDR data (JDK 20+) emits U+202F before AM/PM in localized date formats, while older
     * JDKs (e.g. JDK 17 on CI) emit a plain space. Normalizing before comparison makes assertions
     * tolerant of both, so tests pass regardless of the JDK they run on.
     */
    public static String normalizeSpaces(final String value) {
        return value == null ? null : value.replace(NARROW_NO_BREAK_SPACE, ' ').replace(NO_BREAK_SPACE, ' ');
    }

    /**
     * Like {@link org.junit.Assert#assertEquals(Object, Object)} but ignores differences between
     * narrow/no-break spaces and ordinary spaces. Compares the normalized {@code toString()} of both
     * arguments, so it works for plain strings as well as for objects whose {@code toString()} contains
     * a formatted date.
     */
    public static void assertEqualsIgnoringSpaces(final Object expected, final Object actual) {
        assertEquals(normalizeSpaces(String.valueOf(expected)), normalizeSpaces(String.valueOf(actual)));
    }
}
