package at.rtr.rmbt.service.quality;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for the WebSocket frame codec and helpers in {@link RmbtWebSocketPinger}. The networked
 * {@code ping(...)} itself needs a real RMBT server and is covered by manual/integration testing.
 */
class RmbtWebSocketPingerTest {

    private static final int WS_TEXT = 0x1;
    private static final int WS_PONG = 0xA;

    @Test
    void encodeClientFrame_isMaskedAndRoundTripsThroughReadMessage() throws Exception {
        final byte[] payload = "PING\n".getBytes(StandardCharsets.UTF_8);

        final byte[] frame = RmbtWebSocketPinger.encodeClientFrame(WS_TEXT, payload);

        // FIN + text opcode, and the mask bit (0x80 on the 2nd byte) must be set for client frames.
        assertEquals(0x81, frame[0] & 0xFF, "FIN + text opcode");
        assertTrue((frame[1] & 0x80) != 0, "client frames must be masked");

        // readMessage unmasks and returns the original payload.
        final byte[] decoded = RmbtWebSocketPinger.readMessage(
                new ByteArrayInputStream(frame), new ByteArrayOutputStream());
        assertArrayEquals(payload, decoded);
    }

    @Test
    void readMessage_readsUnmaskedServerTextFrame() throws Exception {
        // Server→client frames are unmasked: 0x81 (FIN+text), len 5, "PONG\n".
        final ByteArrayOutputStream server = new ByteArrayOutputStream();
        server.write(0x81);
        server.write(0x05);
        server.write("PONG\n".getBytes(StandardCharsets.UTF_8));

        final byte[] decoded = RmbtWebSocketPinger.readMessage(
                new ByteArrayInputStream(server.toByteArray()), new ByteArrayOutputStream());

        assertEquals("PONG\n", new String(decoded, StandardCharsets.UTF_8));
    }

    @Test
    void readMessage_answersControlPingWithPongAndSkipsToDataFrame() throws Exception {
        // A control PING (0x89, len 0) followed by a text data frame "OK\n".
        final ByteArrayOutputStream server = new ByteArrayOutputStream();
        server.write(0x89);
        server.write(0x00);
        server.write(0x81);
        server.write(0x03);
        server.write("OK\n".getBytes(StandardCharsets.UTF_8));

        final ByteArrayOutputStream clientOut = new ByteArrayOutputStream();
        final byte[] decoded = RmbtWebSocketPinger.readMessage(
                new ByteArrayInputStream(server.toByteArray()), clientOut);

        assertEquals("OK\n", new String(decoded, StandardCharsets.UTF_8));
        // A PONG control frame (FIN + opcode 0xA = 0x8A) was written back.
        final byte[] reply = clientOut.toByteArray();
        assertTrue(reply.length >= 2, "expected a PONG reply");
        assertEquals(0x80 | WS_PONG, reply[0] & 0xFF);
    }

    @Test
    void hostHeader_bracketsRawIpv6Only() {
        assertEquals("1.2.3.4", RmbtWebSocketPinger.hostHeader("1.2.3.4"));
        assertEquals("host.example.com", RmbtWebSocketPinger.hostHeader("host.example.com"));
        assertEquals("[2001:db8::1]", RmbtWebSocketPinger.hostHeader("2001:db8::1"));
        assertEquals("[2001:db8::1]", RmbtWebSocketPinger.hostHeader("[2001:db8::1]"));
    }
}
