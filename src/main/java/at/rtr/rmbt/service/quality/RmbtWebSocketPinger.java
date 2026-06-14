package at.rtr.rmbt.service.quality;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.Base64;

/**
 * {@link RmbtPinger} that speaks the RMBT protocol over a WebSocket on a TLS socket (the "RMBThttp"
 * transport), mirroring the reference client {@code open-rmbt-client-cli} (RmbtConn):
 *
 * <pre>
 *   TLS connect → HTTP "GET /rmbt" WebSocket upgrade → (text frames carry RMBT lines)
 *   S: RMBTv...                C: TOKEN &lt;token&gt;     S: OK / CHUNKSIZE ...
 *   S: ACCEPT ... PING ...     C: PING   S: PONG     C: OK    S: TIME &lt;ns&gt;
 * </pre>
 *
 * Latency is the client-measured PING→PONG round-trip. Certificates are not verified (measurement
 * servers commonly use self-signed certs). Any failure yields {@link PingOutcome#unreachable()}.
 */
@Component
@Slf4j
public class RmbtWebSocketPinger implements RmbtPinger {

    private static final int CONNECT_TIMEOUT_MS = 5_000;
    private static final int SOCKET_TIMEOUT_MS = 10_000;

    private static final int WS_TEXT = 0x1;
    private static final int WS_PONG = 0xA;
    private static final int WS_PING = 0x9;
    private static final int WS_CLOSE = 0x8;

    @Override
    public PingOutcome ping(final String host, final int port, final String token) {
        try (SSLSocket socket = openTlsSocket(host, port)) {
            socket.setSoTimeout(SOCKET_TIMEOUT_MS);
            socket.setTcpNoDelay(true);
            final OutputStream out = new BufferedOutputStream(socket.getOutputStream(), 4096);
            final InputStream in = socket.getInputStream();

            wsUpgrade(host, in, out);
            greeting(token, in, out);

            // Command prompt — must advertise PING.
            final String accept = readLine(in, out);
            if (!accept.contains("PING")) {
                throw new IOException("Expected ACCEPT with PING, got: " + accept);
            }

            final long t0 = System.nanoTime();
            writeLine("PING", out);
            final String pong = readLine(in, out);
            final long clientNs = System.nanoTime() - t0;
            if (!"PONG".equals(pong)) {
                throw new IOException("Expected PONG, got: " + pong);
            }
            writeLine("OK", out);
            readLine(in, out); // TIME <ns> — server-side timing, not used (we report client RTT)

            final double latencyMs = clientNs / 1_000_000.0;
            log.debug("RMBT ping {}:{} reachable, latency={}ms", host, port, latencyMs);
            return PingOutcome.reachable(latencyMs);
        } catch (Exception e) {
            log.debug("RMBT ping {}:{} failed: {}", host, port, e.toString());
            return PingOutcome.unreachable();
        }
    }

    // ── connection / handshake ──────────────────────────────────────────────────

    private SSLSocket openTlsSocket(final String host, final int port) throws Exception {
        final SSLContext ctx = SSLContext.getInstance("TLS");
        ctx.init(null, new TrustManager[]{trustAll()}, new SecureRandom());
        final SSLSocket socket = (SSLSocket) ctx.getSocketFactory().createSocket();
        socket.connect(new InetSocketAddress(host, port), CONNECT_TIMEOUT_MS);
        socket.setEnabledProtocols(new String[]{"TLSv1.2", "TLSv1.3"});
        socket.startHandshake();
        return socket;
    }

    private void wsUpgrade(final String host, final InputStream in, final OutputStream out) throws IOException {
        final byte[] keyBytes = new byte[16];
        new SecureRandom().nextBytes(keyBytes);
        final String key = Base64.getEncoder().encodeToString(keyBytes);
        final String req = "GET /rmbt HTTP/1.1\r\n" +
                "Host: " + hostHeader(host) + "\r\n" +
                "Connection: Upgrade\r\n" +
                "Upgrade: websocket\r\n" +
                "Sec-WebSocket-Version: 13\r\n" +
                "Sec-WebSocket-Key: " + key + "\r\n" +
                "\r\n";
        out.write(req.getBytes(StandardCharsets.US_ASCII));
        out.flush();
        final String status = readHttpStatusLine(in);
        if (!status.contains("101")) {
            throw new IOException("Expected HTTP 101 for WS upgrade, got: " + status);
        }
    }

    private void greeting(final String token, final InputStream in, final OutputStream out) throws IOException {
        final String version = readLine(in, out).replaceAll("^[\\x00\\s]+", "");
        if (!version.startsWith("RMBTv")) {
            throw new IOException("Unexpected greeting: " + version);
        }
        final String accept = readLine(in, out);
        if (!accept.contains("TOKEN")) {
            throw new IOException("Server did not offer TOKEN: " + accept);
        }
        writeLine("TOKEN " + token, out);
        final String ok = readLine(in, out);
        if (!"OK".equals(ok)) {
            throw new IOException("Token rejected: " + ok);
        }
        readLine(in, out); // CHUNKSIZE ... (ignored)
    }

    /** Bracket a raw IPv6 literal for the HTTP Host header; pass hostnames/IPv4 through unchanged. */
    static String hostHeader(final String host) {
        return (host != null && host.indexOf(':') >= 0 && !host.startsWith("[")) ? "[" + host + "]" : host;
    }

    private static String readHttpStatusLine(final InputStream in) throws IOException {
        final ByteArrayOutputStream buf = new ByteArrayOutputStream(512);
        final int[] tail = {0, 0, 0, 0};
        for (; ; ) {
            final int b = in.read();
            if (b < 0) {
                throw new EOFException("Connection closed reading HTTP headers");
            }
            buf.write(b);
            tail[0] = tail[1];
            tail[1] = tail[2];
            tail[2] = tail[3];
            tail[3] = b;
            if (tail[0] == '\r' && tail[1] == '\n' && tail[2] == '\r' && tail[3] == '\n') {
                break;
            }
            if (buf.size() > 8192) {
                throw new IOException("HTTP headers too large");
            }
        }
        return buf.toString(StandardCharsets.US_ASCII).lines().findFirst().orElse("");
    }

    // ── RMBT line I/O over WebSocket text frames ────────────────────────────────

    private void writeLine(final String s, final OutputStream out) throws IOException {
        out.write(encodeClientFrame(WS_TEXT, (s + "\n").getBytes(StandardCharsets.UTF_8)));
        out.flush();
    }

    private String readLine(final InputStream in, final OutputStream out) throws IOException {
        final String s = new String(readMessage(in, out), StandardCharsets.UTF_8);
        int end = s.length();
        while (end > 0 && (s.charAt(end - 1) == '\n' || s.charAt(end - 1) == '\r')) {
            end--;
        }
        return s.substring(0, end);
    }

    // ── WebSocket framing (package-private + static for unit testing) ────────────

    /**
     * Encode a client→server WebSocket frame: FIN set, masked (RFC 6455 §5.1 requires client frames
     * to be masked), with the given opcode and payload.
     */
    static byte[] encodeClientFrame(final int opcode, final byte[] payload) {
        final int plen = payload.length;
        final ByteArrayOutputStream frame = new ByteArrayOutputStream(plen + 14);
        frame.write(0x80 | (opcode & 0x0F)); // FIN + opcode
        final byte[] mask = new byte[4];
        new SecureRandom().nextBytes(mask);
        if (plen <= 125) {
            frame.write(0x80 | plen);
        } else if (plen <= 65535) {
            frame.write(0x80 | 126);
            frame.write((plen >> 8) & 0xFF);
            frame.write(plen & 0xFF);
        } else {
            frame.write(0x80 | 127);
            for (int i = 7; i >= 0; i--) {
                frame.write((int) (((long) plen >> (i * 8)) & 0xFF));
            }
        }
        frame.write(mask, 0, 4);
        for (int i = 0; i < plen; i++) {
            frame.write(payload[i] ^ mask[i & 3]);
        }
        return frame.toByteArray();
    }

    /**
     * Read one complete WebSocket message (handling fragmentation, masking, and control frames:
     * answers a control PING with a PONG, throws on CLOSE). Returns the (unmasked) payload bytes.
     */
    static byte[] readMessage(final InputStream in, final OutputStream out) throws IOException {
        final ByteArrayOutputStream acc = new ByteArrayOutputStream();
        for (; ; ) {
            final int b0 = in.read();
            final int b1 = in.read();
            if (b0 < 0 || b1 < 0) {
                throw new EOFException("WS connection closed");
            }
            final boolean fin = (b0 & 0x80) != 0;
            final int opcode = b0 & 0x0F;
            final boolean masked = (b1 & 0x80) != 0;
            long plen = b1 & 0x7F;
            if (plen == 126) {
                plen = ((long) (in.read() & 0xFF) << 8) | (in.read() & 0xFF);
            } else if (plen == 127) {
                plen = 0;
                for (int i = 0; i < 8; i++) {
                    plen = (plen << 8) | (in.read() & 0xFF);
                }
            }
            final byte[] mkey = new byte[4];
            if (masked) {
                readExact(in, mkey);
            }
            final byte[] payload = new byte[(int) plen];
            readExact(in, payload);
            if (masked) {
                for (int i = 0; i < payload.length; i++) {
                    payload[i] ^= mkey[i & 3];
                }
            }
            if (opcode == WS_PING) {
                out.write(encodeClientFrame(WS_PONG, payload));
                out.flush();
                continue;
            }
            if (opcode == WS_CLOSE) {
                throw new IOException("WebSocket closed by server");
            }
            acc.write(payload, 0, payload.length);
            if (fin) {
                break;
            }
        }
        return acc.toByteArray();
    }

    private static void readExact(final InputStream in, final byte[] buf) throws IOException {
        int got = 0;
        while (got < buf.length) {
            final int n = in.read(buf, got, buf.length - got);
            if (n < 0) {
                throw new EOFException("Connection closed during read");
            }
            got += n;
        }
    }

    private static X509TrustManager trustAll() {
        return new X509TrustManager() {
            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[0];
            }

            @Override
            public void checkClientTrusted(final X509Certificate[] chain, final String authType) {
            }

            @Override
            public void checkServerTrusted(final X509Certificate[] chain, final String authType) {
            }
        };
    }
}
