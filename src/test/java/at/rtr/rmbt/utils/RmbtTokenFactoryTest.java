package at.rtr.rmbt.utils;

import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RmbtTokenFactoryTest {

    private static final UUID UUID_VALUE = UUID.fromString("11111111-2222-3333-4444-555555555555");
    private static final long TS = 1_700_000_000L;

    @Test
    void createToken_buildsUuidUnderscoreTimestampUnderscoreHmac() {
        final String key = "secret-key";

        final String token = RmbtTokenFactory.createToken(key, UUID_VALUE, TS);

        // The HMAC is computed over "<uuid>_<ts>" with the server key (matches open-rmbt-server token.rs).
        final String data = UUID_VALUE + "_" + TS;
        final String expectedHmac = HelperFunctions.calculateHMAC(key.getBytes(StandardCharsets.UTF_8), data);
        assertEquals(data + "_" + expectedHmac, token);
    }

    @Test
    void createToken_hasThreeFieldsWithUuidAndTimestampFirst() {
        final String token = RmbtTokenFactory.createToken("k", UUID_VALUE, TS);

        assertTrue(token.startsWith(UUID_VALUE + "_" + TS + "_"));
        final String hmac = token.substring((UUID_VALUE + "_" + TS + "_").length());
        assertFalse(hmac.isBlank(), "HMAC segment must not be blank");
        assertFalse(hmac.contains("_"), "HMAC is the final field");
    }

    @Test
    void createToken_differentKeysProduceDifferentTokens() {
        assertNotEquals(
                RmbtTokenFactory.createToken("keyA", UUID_VALUE, TS),
                RmbtTokenFactory.createToken("keyB", UUID_VALUE, TS));
    }
}
