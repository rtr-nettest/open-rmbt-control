package at.rtr.rmbt.service.quality;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;

/**
 * Pinger for QoS test servers ({@code open-rmbt-qos}). The QoS server speaks a TLS line protocol
 * (default port 443): right after the TLS handshake it sends a greeting ({@code QoSSP&lt;version&gt;})
 * followed by {@code ACCEPT [TOKEN string]}. This pinger performs the TLS handshake, reads that
 * greeting to confirm it is really a QoS server, and reports the <b>TCP-connect</b> round-trip as
 * {@code latency_ms}. Certificates are not verified (self-signed is common). No token is needed.
 * Never throws — any failure yields {@link PingOutcome#unreachable()}.
 */
@Component
@Slf4j
public class QosTlsPinger {

    private static final int CONNECT_TIMEOUT_MS = 5_000;
    private static final int SOCKET_TIMEOUT_MS = 8_000;

    /**
     * @param host server hostname/address (use the IPv4- or IPv6-specific address to force the family)
     * @param port TLS port (typically 443)
     */
    public PingOutcome ping(final String host, final int port) {
        final Socket plain = new Socket();
        try {
            final long t0 = System.nanoTime();
            plain.connect(new InetSocketAddress(host, port), CONNECT_TIMEOUT_MS);
            final double latencyMs = (System.nanoTime() - t0) / 1_000_000.0; // TCP-connect round-trip
            plain.setSoTimeout(SOCKET_TIMEOUT_MS);

            final SSLContext ctx = SSLContext.getInstance("TLS");
            ctx.init(null, new TrustManager[]{trustAll()}, new SecureRandom());
            try (SSLSocket ssl = (SSLSocket) ctx.getSocketFactory().createSocket(plain, host, port, true)) {
                ssl.setEnabledProtocols(new String[]{"TLSv1.2", "TLSv1.3"});
                ssl.startHandshake();
                final String greeting = readLine(ssl.getInputStream());
                if (!isQosGreeting(greeting)) {
                    log.debug("QoS ping {}:{} unexpected greeting: {}", host, port, greeting);
                    return PingOutcome.unreachable();
                }
                log.debug("QoS ping {}:{} reachable, latency={}ms (greeting '{}')", host, port, latencyMs, greeting);
                return PingOutcome.reachable(latencyMs);
            }
        } catch (Exception e) {
            log.debug("QoS ping {}:{} failed: {}", host, port, e.toString());
            return PingOutcome.unreachable();
        } finally {
            try {
                plain.close();
            } catch (IOException ignored) {
                // best-effort close
            }
        }
    }

    /** Accepts the QoS greeting ({@code QoSSP...}) or its {@code ACCEPT [TOKEN ...]} prompt. */
    static boolean isQosGreeting(final String line) {
        if (line == null) {
            return false;
        }
        final String s = line.replaceAll("^[\\x00\\s]+", ""); // strip leading NULs/whitespace
        return s.startsWith("QoSSP") || s.toUpperCase().contains("ACCEPT");
    }

    private static String readLine(final InputStream in) throws IOException {
        final ByteArrayOutputStream buf = new ByteArrayOutputStream(64);
        for (; ; ) {
            final int b = in.read();
            if (b < 0) {
                if (buf.size() == 0) {
                    throw new EOFException("connection closed before greeting");
                }
                break;
            }
            if (b == '\n') {
                break;
            }
            if (b == '\r') {
                continue;
            }
            buf.write(b);
            if (buf.size() > 1024) {
                break;
            }
        }
        return buf.toString(StandardCharsets.UTF_8);
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
