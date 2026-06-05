package at.rtr.rmbt.utils;

import java.util.UUID;
import java.util.regex.Pattern;

/**
 * Helpers for filtering client-supplied UUIDs before further processing, so a malformed value
 * (e.g. {@code "46e6e982-d654-4bba-9dc4-orf"}) is treated as "no value" instead of aborting the whole
 * request with a Jackson {@code InvalidFormatException}.
 *
 * <p>The format check is the standard 36-char 8-4-4-4-12 hex representation (ported from the
 * Statistics server's {@code OpenTestRepositoryImpl#isValidUuidFormat}).
 */
public final class UuidUtils {

    private static final Pattern UUID_PATTERN =
            Pattern.compile("^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$");

    private UuidUtils() {
    }

    /** {@code true} only for the standard 36-char 8-4-4-4-12 hex UUID representation. */
    public static boolean isValidUuidFormat(final String uuid) {
        return uuid != null && uuid.length() == 36 && UUID_PATTERN.matcher(uuid).matches();
    }

    /**
     * Parses a client-supplied UUID string, returning {@code null} for anything that is null, blank,
     * the literal {@code "null"}, or not a standard 36-char UUID - instead of throwing. The value is
     * trimmed first.
     */
    public static UUID toUuidOrNull(final String value) {
        if (value == null) {
            return null;
        }
        final String trimmed = value.trim();
        if (!isValidUuidFormat(trimmed)) {
            return null;
        }
        return UUID.fromString(trimmed);
    }
}
