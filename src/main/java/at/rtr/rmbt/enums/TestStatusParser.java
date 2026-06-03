package at.rtr.rmbt.enums;

import java.util.Optional;

/**
 * Lenient parsing of client-supplied {@link TestStatus} values.
 *
 * <p>Clients occasionally submit a status this server does not know (a newer/older client build or a
 * plain bug). Instead of letting Jackson throw {@code InvalidFormatException} - which aborts the
 * whole request with a full stack trace - the raw value is kept as a {@code String} on the request
 * and converted here, so the caller can both react to an unknown value and log a meaningful warning
 * (including the offending client) rather than a stack trace.
 */
public final class TestStatusParser {

    private TestStatusParser() {
    }

    /**
     * @return the matching {@link TestStatus}, or empty if the value is {@code null}/blank or not a
     * known status.
     */
    public static Optional<TestStatus> parse(final String value) {
        if (value == null || value.isBlank()) {
            return Optional.empty();
        }
        try {
            return Optional.of(TestStatus.valueOf(value.trim()));
        } catch (IllegalArgumentException unknown) {
            return Optional.empty();
        }
    }

    /**
     * @return {@code true} if a non-blank value was supplied but does not map to a known
     * {@link TestStatus} (i.e. the client sent an illegal status).
     */
    public static boolean isUnknown(final String value) {
        return value != null && !value.isBlank() && parse(value).isEmpty();
    }
}
