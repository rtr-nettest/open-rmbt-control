package at.rtr.rmbt.utils;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.UUID;

/**
 * Tolerant {@link UUID} deserializer for client-supplied identifiers.
 *
 * <p>An empty/blank value, the literal string {@code "null"}, or any other non-UUID text is treated
 * as <em>no value</em> ({@code null}) instead of aborting the whole request with an
 * {@code InvalidFormatException} (and a logged stack trace). Clients that are not yet registered
 * legitimately send an empty - or, as seen in the wild, a literal {@code "null"} - uuid.
 */
@Slf4j
public class LenientUuidDeserializer extends JsonDeserializer<UUID> {

    @Override
    public UUID deserialize(final JsonParser parser, final DeserializationContext context) throws IOException {
        final String raw = parser.getValueAsString();
        if (raw == null) {
            return null;
        }
        final String value = raw.trim();
        if (value.isEmpty() || "null".equalsIgnoreCase(value)) {
            return null;
        }
        try {
            return UUID.fromString(value);
        } catch (IllegalArgumentException invalid) {
            log.debug("Ignoring malformed client uuid '{}'", value);
            return null;
        }
    }
}
