package at.rtr.rmbt.utils;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

/**
 * Builds RMBT authentication tokens for measurement servers.
 *
 * <p>Token format: {@code <uuid>_<unixSeconds>_<base64(HmacSHA1(uuid_unixSeconds, serverKey))>} —
 * the same scheme the control server uses when handing tokens to clients
 * ({@code TestSettingsFacade}) and that the measurement servers verify (see {@code open-rmbt-server}
 * {@code token.rs}: the HMAC is computed over the string {@code "<uuid>_<timestamp>"}, and the
 * timestamp must fall within the server's accept window).
 */
public final class RmbtTokenFactory {

    private RmbtTokenFactory() {
    }

    /**
     * @param serverKey   the test server's shared secret ({@code test_server.key})
     * @param uuid        a (fresh) UUID for the connection
     * @param unixSeconds the token start time in seconds since the epoch (use "now" for an immediate
     *                    check); the server accepts it only within its early/late window
     * @return the {@code uuid_unixSeconds_hmac} token string
     */
    public static String createToken(final String serverKey, final UUID uuid, final long unixSeconds) {
        final String data = uuid.toString() + "_" + unixSeconds;
        final String hmac = HelperFunctions.calculateHMAC(serverKey.getBytes(StandardCharsets.UTF_8), data);
        return data + "_" + hmac;
    }
}
