package at.rtr.rmbt.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * Tests {@link LenientUuidDeserializer}: a valid UUID parses; empty/blank, JSON null, the literal
 * string "null", and malformed text are all tolerated as {@code null} instead of throwing.
 */
class LenientUuidDeserializerTest {

    private final ObjectMapper mapper = new ObjectMapper();

    static class Holder {
        @JsonDeserialize(using = LenientUuidDeserializer.class)
        public UUID uuid;
    }

    private UUID read(final String uuidJson) throws Exception {
        return mapper.readValue("{\"uuid\":" + uuidJson + "}", Holder.class).uuid;
    }

    @Test
    void validUuidParses() throws Exception {
        final UUID uuid = UUID.fromString("68796996-5f40-11eb-ae93-0242ac130002");
        assertEquals(uuid, read("\"" + uuid + "\""));
    }

    @Test
    void literalNullStringIsNull() throws Exception {
        assertNull(read("\"null\""));
        assertNull(read("\"NULL\""));
    }

    @Test
    void jsonNullIsNull() throws Exception {
        assertNull(read("null"));
    }

    @Test
    void emptyOrBlankIsNull() throws Exception {
        assertNull(read("\"\""));
        assertNull(read("\"   \""));
    }

    @Test
    void malformedUuidIsNull() throws Exception {
        assertNull(read("\"not-a-uuid\""));
        // the exact value that crashed /result (TestResultRequest.test_uuid)
        assertNull(read("\"46e6e982-d654-4bba-9dc4-orf\""));
    }
}
