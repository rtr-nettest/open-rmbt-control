package at.rtr.rmbt.utils;

import at.rtr.rmbt.dto.ASInformation;

import com.google.common.net.InetAddresses;
import lombok.experimental.UtilityClass;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.xbill.DNS.Record;
import org.xbill.DNS.*;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.security.GeneralSecurityException;
import java.time.Duration;
import java.util.*;


@UtilityClass
public class HelperFunctions {

    private static final int DNS_TIMEOUT = 1;
    private static final Logger logger = LoggerFactory.getLogger(HelperFunctions.class);
    /** Base URL of the iptoasn-compatible "AS by IP" HTTP API; the client IP is appended.
     * see <a href="https://github.com/jedisct1/iptoasn-webservice">...</a> */
    private static final String IPTOASN_AS_BY_IP_URL = "http://127.0.0.1:53661/v1/as/ip/";

    public String getTimeZoneId() {
        return TimeZone.getDefault().getID();
    }

    public static String getRoamingType(final MessageSource messageSource, final int roamingType, Locale locale) {
        return switch (roamingType) {
            case 0 -> messageSource.getMessage("value_roaming_none", null, locale);
            case 1 -> messageSource.getMessage("value_roaming_national", null, locale);
            case 2 -> messageSource.getMessage("value_roaming_international", null, locale);
            default -> "?";
        };
    }

    public static String geoToString(final Double geoLat, final Double geoLong) {

        if (geoLat == null || geoLong == null)
            return null;

        int latd, lond; // latitude degrees and minutes, longitude degrees and
        // minutes
        double latm, lonm; // latitude and longitude seconds.

        // decimal degrees to degrees minutes seconds

        double temp;
        // latitude
        temp = Math.abs(geoLat);
        latd = (int) temp;
        latm = (temp - latd) * 60.0;

        // longitude
        temp = Math.abs(geoLong);
        lond = (int) temp;
        lonm = (temp - lond) * 60.0;

        final String dirLat;
        if (geoLat >= 0)
            dirLat = "N";
        else
            dirLat = "S";
        final String dirLon;
        if (geoLong >= 0)
            dirLon = "E";
        else
            dirLon = "W";

        return String.format("%s %2d°%02.3f'  %s %2d°%02.3f'", dirLat, latd, latm, dirLon, lond, lonm);
    }

    public static String getNetworkTypeName(final Integer type) {
        if (type == null)
            return "UNKNOWN";
        return switch (type) {
            case 1, 16 -> "2G (GSM)";
            case 2 -> "2G (EDGE)";
            case 3 -> "3G (UMTS)";
            case 4 -> "2G (CDMA)";
            case 5 -> "2G (EVDO_0)";
            case 6 -> "2G (EVDO_A)";
            case 7 -> "2G (1xRTT)";
            case 8 -> "3G (HSDPA)";
            case 9 -> "3G (HSUPA)";
            case 10 -> "3G (HSPA)";
            case 11 -> "2G (IDEN)";
            case 12 -> "2G (EVDO_B)";
            case 13 -> "4G (LTE)";
            case 14 -> "2G (EHRPD)";
            case 15 -> "3G (HSPA+)";
            case 19 -> "4G (LTE CA)";
            case 20 -> "5G (NR)";
            case 40 -> "4G (+5G)";
            case 41 -> "5G (NR)";
            case 42 -> "5G (NR)";
            case 97 -> "CLI";
            case 98 -> "BROWSER";
            case 99 -> "WLAN";
            case 101 -> "2G/3G";
            case 102 -> "3G/4G";
            case 103 -> "2G/4G";
            case 104 -> "2G/3G/4G";
            case 105 -> "MOBILE";
            case 106 -> "Ethernet";
            case 107 -> "Bluetooth";
            default -> "UNKNOWN";
        };
    }

    public static String calculateHMAC(final byte[] secret, final String data) {
        try {
            final SecretKeySpec signingKey = new SecretKeySpec(secret, "HmacSHA1");
            final Mac mac = Mac.getInstance("HmacSHA1");
            mac.init(signingKey);
            final byte[] rawHmac = mac.doFinal(data.getBytes());
            return Base64.getEncoder().encodeToString(rawHmac);
        } catch (final GeneralSecurityException e) {

            logger.error("Unexpected error while creating hash: {}", e.getMessage());
            return "";
        }
    }

    public byte[] calculateSha256HMAC(final byte[] secret, final byte[] data) {
        try {
            final SecretKeySpec signingKey = new SecretKeySpec(secret, "HmacSHA256");
            final Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(signingKey);
            return mac.doFinal(data);
        } catch (final GeneralSecurityException e) {

            logger.error("Error while creating 2 arg sha256-hash: {}", e.getMessage());
            int size = 16;
            return new byte[size];
        }
    }

    public byte[] calculateSha256HMAC(final byte[] secret, final byte[] data1, final byte[] data2) {
        try {
            final SecretKeySpec signingKey = new SecretKeySpec(secret, "HmacSHA256");
            final Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(signingKey);
            mac.update(data1);
            mac.update(data2);
            return mac.doFinal();
        } catch (final GeneralSecurityException e) {

            logger.error("Error while creating 3 arg sha256-hash: {}", e.getMessage());
            int size = 16;
            return new byte[size];
        }
    }


    /**
     * Reverse-DNS (PTR) lookup of an IP address. Best-effort: returns {@code null} on any failure
     * or timeout. The result has no trailing dot (e.g. {@code dns.google}, not {@code dns.google.}).
     */
    public static String reverseDNSLookup(final InetAddress adr) {
        try {
            final Lookup lookup = new Lookup(ReverseMap.fromAddress(adr), Type.PTR);
            final SimpleResolver resolver = new SimpleResolver();
            resolver.setTimeout(Duration.ofSeconds(DNS_TIMEOUT));
            lookup.setResolver(resolver);
            final Record[] records = lookup.run();
            if (lookup.getResult() == Lookup.SUCCESSFUL && records != null) {
                for (final Record record : records) {
                    if (record instanceof PTRRecord ptr) {
                        return stripTrailingDot(ptr.getTarget().toString());
                    }
                }
            }
        } catch (final Exception ignored) {
            // reverse DNS is best-effort; resolution failures are non-fatal
        }
        return null;
    }

    /** Removes a single trailing dot so dnsjava's FQDN {@code "dns.google."} becomes {@code "dns.google"}. */
    static String stripTrailingDot(final String host) {
        if (host == null) {
            return null;
        }
        return host.endsWith(".") ? host.substring(0, host.length() - 1) : host;
    }

    public String anonymizeIp(final InetAddress inetAddress) {
        try {
            final byte[] address = inetAddress.getAddress();
            address[address.length - 1] = 0;
            if (address.length > 4) {
                for (int i = 6; i < address.length; i++)
                    address[i] = 0;
            }

            String result = InetAddresses.toAddrString(InetAddress.getByAddress(address));
            if (address.length == 4)
                result = result.replaceFirst(".0$", "");
            return result;
        } catch (final Exception e) {
            logger.error("Failed to anonymize IP address", e);
            return null;
        }
    }

    private static Long getASN(final InetAddress adr) {
        final Name postfix = (adr instanceof Inet6Address)
                ? Name.fromConstantString("origin6.asn.cymru.com")
                : Name.fromConstantString("origin.asn.cymru.com");
        return parseCymruAsn(cymruTxtLine(getReverseIPName(adr, postfix), 3));
    }

    public static Name getReverseIPName(final InetAddress adr, final Name postfix) {
        final byte[] addr = adr.getAddress();
        final StringBuilder sb = new StringBuilder();
        if (addr.length == 4)
            for (int i = addr.length - 1; i >= 0; i--) {
                sb.append(addr[i] & 0xFF);
                if (i > 0)
                    sb.append(".");
            }
        else {
            final int[] nibbles = new int[2];
            for (int i = addr.length - 1; i >= 0; i--) {
                nibbles[0] = (addr[i] & 0xFF) >> 4;
                nibbles[1] = addr[i] & 0xFF & 0xF;
                for (int j = nibbles.length - 1; j >= 0; j--) {
                    sb.append(Integer.toHexString(nibbles[j]));
                    if (i > 0 || j > 0)
                        sb.append(".");
                }
            }
        }
        try {
            return Name.fromString(sb.toString(), postfix);
        } catch (final TextParseException e) {
            throw new IllegalStateException("name cannot be invalid");
        }
    }

    // Cymru AS line: "<asn> | <country> | <registry> | <date> | <name>"
    private static String getASName(final long asn) {
        return cymruField(cymruTxtLine(asnCymruName(asn), 0), 4);
    }

    // Cymru AS line: "<asn> | <country> | <registry> | <date> | <name>"
    private static String getAScountry(final long asn) {
        return cymruField(cymruTxtLine(asnCymruName(asn), 0), 1);
    }

    /** Builds the Cymru AS lookup name, e.g. {@code AS15169.asn.cymru.com}. */
    private static Name asnCymruName(final long asn) {
        try {
            return new Name(String.format("AS%d", asn), Name.fromConstantString("asn.cymru.com."));
        } catch (final TextParseException e) {
            throw new IllegalStateException("AS name cannot be invalid", e);
        }
    }

    /**
     * Runs a Cymru TXT lookup and returns the first TXT record's pipe-delimited "verbose" line, or
     * {@code null}. Network call; best-effort (any failure/timeout returns {@code null}).
     */
    private static String cymruTxtLine(final Name name, final int timeoutSeconds) {
        try {
            final Lookup lookup = new Lookup(name, Type.TXT);
            final SimpleResolver resolver = new SimpleResolver();
            if (timeoutSeconds > 0) {
                resolver.setTimeout(Duration.ofSeconds(timeoutSeconds));
            }
            lookup.setResolver(resolver);
            final Record[] records = lookup.run();
            if (lookup.getResult() == Lookup.SUCCESSFUL && records != null) {
                for (final Record record : records) {
                    if (record instanceof TXTRecord txt) {
                        final List<String> strings = txt.getStrings();
                        if (strings != null && !strings.isEmpty()) {
                            return strings.get(0);
                        }
                    }
                }
            }
        } catch (final Exception ignored) {
            // best-effort
        }
        return null;
    }

    /**
     * Returns the trimmed field at {@code index} of a Cymru pipe-delimited line, or {@code null} if
     * the line is null or has fewer fields (no {@link ArrayIndexOutOfBoundsException}).
     */
    static String cymruField(final String txtLine, final int index) {
        if (txtLine == null) {
            return null;
        }
        final String[] parts = txtLine.split(" ?\\| ?");
        return index >= 0 && index < parts.length ? parts[index].trim() : null;
    }

    /** Parses the first ASN of a Cymru origin line (field 0 may list several, space-separated). */
    static Long parseCymruAsn(final String txtLine) {
        final String first = cymruField(txtLine, 0);
        if (first == null || first.isBlank()) {
            return null;
        }
        try {
            return Long.parseLong(first.split(" ")[0]);
        } catch (final NumberFormatException e) {
            return null;
        }
    }

    /** Strategy for resolving {@link ASInformation} for an IP address; returns {@code null} when it cannot. */
    @FunctionalInterface
    private interface AsInformationResolver {
        ASInformation resolve(InetAddress address);
    }

    /**
     * AS information (number/name/country) for an IP address. Prioritizes the local MaxMind databases
     * and falls back to the cymru.com DNS API. Used for all test types (the "...ForSignalRequest" name
     * is historical). Always returns a non-null {@link ASInformation}; its fields are null when nothing
     * resolves.
     */
    public static ASInformation getASInformationForSignalRequest(final InetAddress addr) {
        final AsInformationResolver[] resolvers = {
                HelperFunctions::asInformationFromMaxMind, // 1) local MaxMind databases (preferred)
                HelperFunctions::asInformationFromCymru,   // 2) cymru.com DNS API (fallback)
                HelperFunctions::asInformationFromIpToAsn  // 3) iptoasn (2nd fallback)
        };
        for (final AsInformationResolver resolver : resolvers) {
            final ASInformation info = resolver.resolve(addr);
            if (info != null && info.getNumber() != null) {
                return info;
            }
        }
        return ASInformation.builder().build();
    }

    /** A single AS-information source, for {@link #getASInformation(InetAddress, AsSource)}. */
    public enum AsSource {
        MAXMIND, CYMRU, IPTOASN
    }

    /**
     * Resolves AS information from one explicitly chosen source; returns {@code null} when that source
     * has no data or is unreachable. For normal use prefer {@link #getASInformationForSignalRequest},
     * which prioritizes the sources. This single-source entry point exists mainly so each resolver can
     * be exercised directly (e.g. by integration tests) without reflection.
     */
    public static ASInformation getASInformation(final InetAddress addr, final AsSource source) {
        return switch (source) {
            case MAXMIND -> asInformationFromMaxMind(addr);
            case CYMRU -> asInformationFromCymru(addr);
            case IPTOASN -> asInformationFromIpToAsn(addr);
        };
    }

    /**
     * AS info from the local MaxMind databases (number + name). The MaxMind ASN database carries no AS
     * registration country, so the country is enriched from cymru.com by ASN. Returns {@code null} if
     * MaxMind has no ASN for the address.
     */
    private static ASInformation asInformationFromMaxMind(final InetAddress addr) {
        final GeoIpHelper.AsnInfo asnInfo = GeoIpHelper.lookupAsn(addr);
        if (asnInfo == null || asnInfo.autonomousSystemNumber == null) {
            return null;
        }
        final Long asn = asnInfo.autonomousSystemNumber;
        return ASInformation.builder()
                .number(asn)
                .name(asnInfo.autonomousSystemOrganization)
                .country(getAScountry(asn))
                .build();
    }

    /** AS info entirely from the cymru.com DNS API (number, name, country). Returns {@code null} if no ASN. */
    private static ASInformation asInformationFromCymru(final InetAddress addr) {
        final Long asn = getASN(addr);
        if (asn == null) {
            return null;
        }
        return ASInformation.builder()
                .number(asn)
                .name(getASName(asn))
                .country(getAScountry(asn))
                .build();
    }

    /**
     * AS info from the iptoasn.com HTTP API. Deprecated (stringent rate limiting); retained as an
     * alternative {@link AsInformationResolver}.
     */
    private static ASInformation asInformationFromIpToAsn(final InetAddress addr) {
        try {
            final HttpURLConnection urlConnection = (HttpURLConnection)
                    new URL(IPTOASN_AS_BY_IP_URL + addr.getHostAddress()).openConnection();
            urlConnection.setConnectTimeout(3000);
            urlConnection.setRequestProperty("Accept", "application/json");
            urlConnection.setRequestProperty("User-Agent", "curl/7.47.0");
            final StringBuilder sb = new StringBuilder();
            try (final BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()))) {
                int read;
                final char[] chars = new char[1024];
                while ((read = reader.read(chars)) != -1) {
                    sb.append(chars, 0, read);
                }
            }
            final JSONObject jo = new JSONObject(sb.toString());
            if (jo.optLong("as_number", 0) <= 0) {
                return null;
            }
            return ASInformation.builder()
                    .number(jo.optLong("as_number", 0))
                    .name(jo.optString("as_description", null))
                    .country(jo.optString("as_country_code", null))
                    .build();
        } catch (final JSONException | IOException e) {
            logger.warn("iptoasn.com AS lookup failed: {}", e.getMessage());
        }
        return null;
    }

    public static String getNatType(final InetAddress localAdr, final InetAddress publicAdr) {
        try {
            final String ipVersionLocal;
            final String ipVersionPublic;
            if (publicAdr instanceof Inet4Address)
                ipVersionPublic = "ipv4";
            else if (publicAdr instanceof Inet6Address)
                ipVersionPublic = "ipv6";
            else
                ipVersionPublic = "ipv?";

            // in case there is no info regarding local address, store public ip version
            if (localAdr == null) {
                return "is_"+ipVersionPublic;
            }

            if (localAdr instanceof Inet4Address)
                ipVersionLocal = "ipv4";
            else if (localAdr instanceof Inet6Address)
                ipVersionLocal = "ipv6";
            else
                ipVersionLocal = "ipv?";

            if (localAdr.equals(publicAdr))
                return "no_nat_" + ipVersionPublic;
            else {
                final String localType = isIPLocal(localAdr) ? "local" : "public";
                final String publicType = isIPLocal(publicAdr) ? "local" : "public";
                if (ipVersionLocal.equals(ipVersionPublic)) {
                    return String.format("nat_%s_to_%s_%s", localType, publicType, ipVersionPublic);
                } else {
                    return String.format("nat_%s_to_%s_%s", ipVersionLocal, publicType, ipVersionPublic);
                }
            }
        } catch (final IllegalArgumentException e) {
            return "illegal_ip";
        }
    }

    public static boolean isIPLocal(final InetAddress adr) {
        return adr.isLinkLocalAddress() || adr.isLoopbackAddress() || adr.isSiteLocalAddress();
    }

    public static String IpType(InetAddress inetAddress) {
        try {
            final String ipVersion;
            if (inetAddress instanceof Inet4Address)
                ipVersion = "ipv4";
            else if (inetAddress instanceof Inet6Address)
                ipVersion = "ipv6";
            else
                ipVersion = "ipv?";

            if (inetAddress.isAnyLocalAddress())
                return "wildcard_" + ipVersion;
            if (inetAddress.isSiteLocalAddress())
                return "site_local_" + ipVersion;
            if (inetAddress.isLinkLocalAddress())
                return "link_local_" + ipVersion;
            if (inetAddress.isLoopbackAddress())
                return "loopback_" + ipVersion;
            return "public_" + ipVersion;

        } catch (final IllegalArgumentException e) {
            return "illegal_ip";
        }
    }
}
