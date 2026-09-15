package at.rtr.rmbt.service.impl;

import at.rtr.rmbt.model.ProxyInfo;
import at.rtr.rmbt.properties.IcloudEgressProperties;
import at.rtr.rmbt.service.PrivateRelayService;
import com.google.common.net.InetAddresses;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.net.InetAddress;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeSet;

/**
 * Downloads Apple's iCloud Private Relay egress IP-range list every 24 hours and answers per-IP
 * "is this a Private Relay egress?" lookups from a Redis cache.
 *
 * <h2>Source</h2>
 * A header-less CSV ({@code prefix,proxy_country,proxy_region,proxy_city,...}), e.g.
 * <pre>
 * 104.28.135.1/32,AT,AT-09,Vienna,
 * 2606:54c3:0:1690::/64,DE,DE-NW,Bocholt,
 * </pre>
 *
 * <h2>Redis structure</h2>
 * Two hashes, one per address family — {@code proxy:relay:v4} and {@code proxy:relay:v6}:
 * <ul>
 *   <li>a reserved field {@code #lengths} holding the comma-separated distinct prefix lengths
 *       present in that family (e.g. {@code "24,32"});</li>
 *   <li>one field per prefix, keyed {@code <prefixLength>|<hex-of-masked-network-bytes>}, whose
 *       value is {@code proxy_country  proxy_region  proxy_city  ipv6}.</li>
 * </ul>
 * Splitting entries by exact prefix length lets a lookup mask the query IP to each present length
 * and probe those fields with a single {@code HMGET} — O(number of distinct prefix lengths), which
 * for this data set is a handful — instead of scanning every range. The trailing {@code ipv6}
 * boolean is stored per entry as required (always {@code false} for IPv4, {@code true} for IPv6).
 *
 * <h2>Atomic, non-destructive refresh</h2>
 * Each refresh builds a {@code :staging} hash and swaps it in with {@code RENAME}, so readers never
 * see a half-written set. If the download fails or parses to zero entries, the previous data is
 * kept (the store is never dropped on an empty/failed result).
 */
@Slf4j
@Service
public class PrivateRelayServiceImpl implements PrivateRelayService {

    static final String KEY_PREFIX = "proxy:relay:";
    static final String FAMILY_V4 = "v4";
    static final String FAMILY_V6 = "v6";
    static final String LENGTHS_FIELD = "#lengths";
    static final String VALUE_DELIMITER = String.valueOf((char) 1);
    private static final int HSET_BATCH = 5_000;

    private final StringRedisTemplate redis;
    private final IcloudEgressProperties properties;
    private final HttpClient httpClient;

    public PrivateRelayServiceImpl(StringRedisTemplate redis, IcloudEgressProperties properties) {
        this.redis = redis;
        this.properties = properties;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(properties.getConnectTimeout())
                .followRedirects(HttpClient.Redirect.NORMAL)
                .build();
    }

    // ---------------------------------------------------------------------------------------------
    // Scheduled refresh
    // ---------------------------------------------------------------------------------------------

    @Scheduled(
            initialDelayString = "${icloud-egress.initial-delay:PT20S}",
            fixedDelayString = "${icloud-egress.fixed-delay:PT24H}")
    public void refresh() {
        if (!properties.isEnabled()) {
            log.info("iCloud Private Relay egress refresh is disabled");
            return;
        }
        try {
            String csv = download();
            store(csv);
        } catch (Exception e) {
            // Any failure (network, HTTP error, Redis outage) leaves the previously loaded data in place.
            log.error("iCloud Private Relay egress refresh failed, keeping previous data: {}", e.toString());
        }
    }

    private String download() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(properties.getUrl()))
                .timeout(properties.getRequestTimeout())
                .header("Accept", "text/csv")
                .GET()
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        if (response.statusCode() != 200) {
            throw new IllegalStateException("HTTP " + response.statusCode() + " from " + properties.getUrl());
        }
        return response.body();
    }

    private void store(String csv) {
        FamilyData v4 = new FamilyData();
        FamilyData v6 = new FamilyData();
        parse(csv, v4, v6);

        int total = v4.entries.size() + v6.entries.size();
        if (total == 0) {
            log.warn("iCloud Private Relay egress CSV parsed to 0 usable prefixes, keeping previous data");
            return;
        }
        writeFamily(FAMILY_V4, v4);
        writeFamily(FAMILY_V6, v6);
        log.info("iCloud Private Relay egress ranges refreshed: {} IPv4 and {} IPv6 prefixes", v4.entries.size(), v6.entries.size());
    }

    private void parse(String csv, FamilyData v4, FamilyData v6) {
        for (String line : csv.split("\r?\n")) {
            if (line.isBlank()) {
                continue;
            }
            String[] p = line.split(",", -1);
            String prefix = p[0].trim();
            if (prefix.isEmpty() || prefix.indexOf('/') < 0) {
                continue;
            }
            String proxyCountry = p.length > 1 ? p[1].trim() : "";
            String proxyRegion = p.length > 2 ? p[2].trim() : "";
            String proxyCity = p.length > 3 ? p[3].trim() : "";
            try {
                Cidr cidr = parseCidr(prefix);
                FamilyData family = cidr.ipv6 ? v6 : v4;
                family.entries.put(fieldFor(cidr.prefixLength, cidr.network), valueFor(proxyCountry, proxyRegion, proxyCity, cidr.ipv6));
                family.lengths.add(cidr.prefixLength);
            } catch (RuntimeException e) {
                log.debug("Skipping invalid CIDR '{}': {}", prefix, e.getMessage());
            }
        }
    }

    /** Builds the new hash under a staging key and swaps it in atomically via RENAME. */
    private void writeFamily(String family, FamilyData data) {
        if (data.entries.isEmpty()) {
            // Never wipe a family for which the fresh download carries no entries.
            return;
        }
        String live = KEY_PREFIX + family;
        String staging = live + ":staging";
        redis.delete(staging);

        Map<String, String> fields = new LinkedHashMap<>(data.entries);
        fields.put(LENGTHS_FIELD, joinLengths(data.lengths));

        Map<String, String> batch = new HashMap<>();
        for (Map.Entry<String, String> e : fields.entrySet()) {
            batch.put(e.getKey(), e.getValue());
            if (batch.size() >= HSET_BATCH) {
                redis.<String, String>opsForHash().putAll(staging, batch);
                batch.clear();
            }
        }
        if (!batch.isEmpty()) {
            redis.<String, String>opsForHash().putAll(staging, batch);
        }
        redis.rename(staging, live);
    }

    // ---------------------------------------------------------------------------------------------
    // Lookup
    // ---------------------------------------------------------------------------------------------

    @Override
    public Optional<ProxyInfo> lookup(InetAddress ip) {
        if (ip == null) {
            return Optional.empty();
        }
        byte[] bytes = ip.getAddress();
        String family = bytes.length == 16 ? FAMILY_V6 : FAMILY_V4;
        String key = KEY_PREFIX + family;
        try {
            Object lengthsValue = redis.opsForHash().get(key, LENGTHS_FIELD);
            if (lengthsValue == null) {
                return Optional.empty();
            }
            int[] lengths = parseLengthsDescending(lengthsValue.toString());
            List<Object> queryFields = new ArrayList<>(lengths.length);
            for (int len : lengths) {
                queryFields.add(fieldFor(len, maskBytes(bytes, len)));
            }
            // multiGet preserves the query order (longest prefix first), so the first hit wins.
            List<Object> values = redis.opsForHash().multiGet(key, queryFields);
            for (Object value : values) {
                if (value != null) {
                    return Optional.of(parseValue(value.toString()));
                }
            }
            return Optional.empty();
        } catch (Exception e) {
            // A lookup must never break the /ip endpoint: on any store error, treat IP as "not a proxy".
            log.warn("Private Relay lookup failed for {}: {}", ip.getHostAddress(), e.toString());
            return Optional.empty();
        }
    }

    // ---------------------------------------------------------------------------------------------
    // CIDR / encoding helpers
    // ---------------------------------------------------------------------------------------------

    private static Cidr parseCidr(String cidr) {
        int slash = cidr.indexOf('/');
        String address = cidr.substring(0, slash);
        int prefixLength = Integer.parseInt(cidr.substring(slash + 1).trim());
        byte[] bytes = InetAddresses.forString(address).getAddress();
        boolean ipv6 = bytes.length == 16;
        int maxLength = bytes.length * 8;
        if (prefixLength < 0 || prefixLength > maxLength) {
            throw new IllegalArgumentException("prefix length " + prefixLength + " out of range for " + address);
        }
        return new Cidr(ipv6, prefixLength, maskBytes(bytes, prefixLength));
    }

    /** Returns a copy of {@code bytes} with all bits beyond {@code prefixLength} cleared. */
    private static byte[] maskBytes(byte[] bytes, int prefixLength) {
        byte[] out = new byte[bytes.length];
        for (int i = 0; i < bytes.length; i++) {
            int bitsHere = prefixLength - (i * 8);
            if (bitsHere >= 8) {
                out[i] = bytes[i];
            } else if (bitsHere > 0) {
                int mask = (0xFF << (8 - bitsHere)) & 0xFF;
                out[i] = (byte) (bytes[i] & mask);
            } // else: whole byte is beyond the prefix -> stays 0
        }
        return out;
    }

    private static String fieldFor(int prefixLength, byte[] network) {
        return prefixLength + "|" + toHex(network);
    }

    private static String valueFor(String country, String region, String city, boolean ipv6) {
        return country + VALUE_DELIMITER + region + VALUE_DELIMITER + city + VALUE_DELIMITER + ipv6;
    }

    private static ProxyInfo parseValue(String stored) {
        String[] parts = stored.split(VALUE_DELIMITER, -1);
        String country = parts.length > 0 ? emptyToNull(parts[0]) : null;
        String region = parts.length > 1 ? emptyToNull(parts[1]) : null;
        String city = parts.length > 2 ? emptyToNull(parts[2]) : null;
        boolean ipv6 = parts.length > 3 && Boolean.parseBoolean(parts[3]);
        return new ProxyInfo(country, region, city, ipv6);
    }

    private static String joinLengths(TreeSet<Integer> lengths) {
        StringBuilder sb = new StringBuilder();
        for (Integer length : lengths) {
            if (!sb.isEmpty()) {
                sb.append(',');
            }
            sb.append(length);
        }
        return sb.toString();
    }

    /** Parses the {@code #lengths} field into distinct lengths, sorted descending (longest first). */
    private static int[] parseLengthsDescending(String lengths) {
        TreeSet<Integer> sorted = new TreeSet<>();
        for (String token : lengths.split(",")) {
            token = token.trim();
            if (!token.isEmpty()) {
                sorted.add(Integer.parseInt(token));
            }
        }
        int[] result = new int[sorted.size()];
        int i = 0;
        for (Integer length : sorted.descendingSet()) {
            result[i++] = length;
        }
        return result;
    }

    private static String toHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) {
            sb.append(Character.forDigit((b >> 4) & 0xF, 16));
            sb.append(Character.forDigit(b & 0xF, 16));
        }
        return sb.toString();
    }

    private static String emptyToNull(String s) {
        return (s == null || s.isEmpty()) ? null : s;
    }

    /** Accumulates the parsed prefixes of one address family plus its distinct prefix lengths. */
    private static final class FamilyData {
        private final Map<String, String> entries = new LinkedHashMap<>();
        private final TreeSet<Integer> lengths = new TreeSet<>();
    }

    private record Cidr(boolean ipv6, int prefixLength, byte[] network) {
    }
}
