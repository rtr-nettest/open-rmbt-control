package at.rtr.rmbt.utils;

import at.rtr.rmbt.dto.ASInformation;
import com.google.common.net.InetAddresses;
import lombok.experimental.UtilityClass;
import org.geotools.util.Base64;
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
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.TimeZone;

@UtilityClass
public class HelperFunctions {

    private static final int DNS_TIMEOUT = 1;
    private static Logger logger = LoggerFactory.getLogger(HelperFunctions.class);

    public String getTimeZoneId() {
        return TimeZone.getDefault().getID();
    }

    public static String getRoamingType(final MessageSource messageSource, final int roamingType, Locale locale) {
        switch (roamingType) {
            case 0:
                return messageSource.getMessage("value_roaming_none", null, locale);
            case 1:
                return messageSource.getMessage("value_roaming_national", null, locale);
            case 2:
                return messageSource.getMessage("value_roaming_international", null, locale);
        }
        return "?";
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
        switch (type) {
            case 1:
            case 16:
                return "2G (GSM)";
            case 2:
                return "2G (EDGE)";
            case 3:
                return "3G (UMTS)";
            case 4:
                return "2G (CDMA)";
            case 5:
                return "2G (EVDO_0)";
            case 6:
                return "2G (EVDO_A)";
            case 7:
                return "2G (1xRTT)";
            case 8:
                return "3G (HSDPA)";
            case 9:
                return "3G (HSUPA)";
            case 10:
                return "3G (HSPA)";
            case 11:
                return "2G (IDEN)";
            case 12:
                return "2G (EVDO_B)";
            case 13:
                return "4G (LTE)";
            case 14:
                return "2G (EHRPD)";
            case 15:
                return "3G (HSPA+)";
            case 19:
                return "4G (LTE CA)";
            case 20:
                return "5G (NR)";
            case 40:
                return "4G (+5G)";
            case 41:
                return "5G (NR)";
            case 42:
                return "5G (NR)";
            case 97:
                return "CLI";
            case 98:
                return "BROWSER";
            case 99:
                return "WLAN";
            case 101:
                return "2G/3G";
            case 102:
                return "3G/4G";
            case 103:
                return "2G/4G";
            case 104:
                return "2G/3G/4G";
            case 105:
                return "MOBILE";
            case 106:
                return "Ethernet";
            case 107:
                return "Bluetooth";
            default:
                return "UNKNOWN";
        }
    }

    public static String calculateHMAC(final byte[] secret, final String data) {
        try {
            final SecretKeySpec signingKey = new SecretKeySpec(secret, "HmacSHA1");
            final Mac mac = Mac.getInstance("HmacSHA1");
            mac.init(signingKey);
            final byte[] rawHmac = mac.doFinal(data.getBytes());
            return Base64.encodeBytes(rawHmac);
        } catch (final GeneralSecurityException e) {

            logger.error("Unexpected error while creating hash: " + e.getMessage());
            return "";
        }
    }

    public static String reverseDNSLookup(final InetAddress adr) {
        try {
            final Name name = ReverseMap.fromAddress(adr);

            final Lookup lookup = new Lookup(name, Type.PTR);
            SimpleResolver simpleResolver = new SimpleResolver();
            simpleResolver.setTimeout(DNS_TIMEOUT);
            lookup.setResolver(simpleResolver);
            lookup.setCache(null);
            final Record[] records = lookup.run();
            if (lookup.getResult() == Lookup.SUCCESSFUL)
                for (final Record record : records)
                    if (record instanceof PTRRecord) {
                        final PTRRecord ptr = (PTRRecord) record;
                        return ptr.getTarget().toString();
                    }
        } catch (final Exception ignored) {
        }
        return null;
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
            e.printStackTrace();
            return null;
        }
    }

    public static ASInformation getASInformation(final InetAddress addr) {
        try {
            String ipAsString = addr.getHostAddress();

            final HttpURLConnection urlConnection = (HttpURLConnection) new URL("https://api.iptoasn.com/v1/as/ip/" + ipAsString).openConnection();
            urlConnection.setConnectTimeout(3000);
            urlConnection.setRequestProperty("Accept", "application/json");
            urlConnection.setRequestProperty("User-Agent", "curl/7.47.0");
            final StringBuilder stringBuilder = new StringBuilder();
            final BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            int read;
            final char[] chars = new char[1024];
            while ((read = reader.read(chars)) != -1) {
                stringBuilder.append(chars, 0, read);
            }

            JSONObject jo = new JSONObject(stringBuilder.toString());

            if (jo.optLong("as_number", 0) <= 0) {
                return null;
            }
            return ASInformation.builder()
                    .name(jo.optString("as_description", null))
                    .country(jo.optString("as_country_code", null))
                    .number(jo.optLong("as_number", 0))
                    .build();

        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getReverseDNS(final InetAddress adr) {
        try {
            final Name name = ReverseMap.fromAddress(adr);

            final Lookup lookup = new Lookup(name, Type.PTR);
            SimpleResolver simpleResolver = new SimpleResolver();
            simpleResolver.setTimeout(DNS_TIMEOUT);
            lookup.setResolver(simpleResolver);
            lookup.setCache(null);
            final Record[] records = lookup.run();
            if (lookup.getResult() == Lookup.SUCCESSFUL)
                for (final Record record : records)
                    if (record instanceof PTRRecord) {
                        final PTRRecord ptr = (PTRRecord) record;
                        return ptr.getTarget().toString();
                    }
        } catch (final Exception e) {
        }
        return null;
    }

    public static Long getASN(final InetAddress adr) {
        try {
            final Name postfix;
            if (adr instanceof Inet6Address)
                postfix = Name.fromConstantString("origin6.asn.cymru.com");
            else
                postfix = Name.fromConstantString("origin.asn.cymru.com");

            final Name name = getReverseIPName(adr, postfix);
            System.out.println("lookup: " + name);

            final Lookup lookup = new Lookup(name, Type.TXT);
            SimpleResolver resolver = new SimpleResolver();
            resolver.setTimeout(3);
            lookup.setResolver(resolver);
            lookup.setCache(null);
            final Record[] records = lookup.run();
            if (lookup.getResult() == Lookup.SUCCESSFUL)
                for (final Record record : records)
                    if (record instanceof TXTRecord) {
                        final TXTRecord txt = (TXTRecord) record;
                        @SuppressWarnings("unchecked") final List<String> strings = txt.getStrings();
                        if (strings != null && !strings.isEmpty()) {
                            final String result = strings.get(0);
                            final String[] parts = result.split(" ?\\| ?");
                            if (parts != null && parts.length >= 1)
                                return new Long(parts[0].split(" ")[0]);
                        }
                    }
        } catch (final Exception e) {
        }
        return null;
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

    public static String getASName(final long asn) {
        try {
            final Name postfix = Name.fromConstantString("asn.cymru.com.");
            final Name name = new Name(String.format("AS%d", asn), postfix);
            System.out.println("lookup: " + name);

            final Lookup lookup = new Lookup(name, Type.TXT);
            lookup.setResolver(new SimpleResolver());
            lookup.setCache(null);
            final Record[] records = lookup.run();
            if (lookup.getResult() == Lookup.SUCCESSFUL)
                for (final Record record : records)
                    if (record instanceof TXTRecord) {
                        final TXTRecord txt = (TXTRecord) record;
                        @SuppressWarnings("unchecked") final List<String> strings = txt.getStrings();
                        if (strings != null && !strings.isEmpty()) {
                            System.out.println(strings);

                            final String result = strings.get(0);
                            final String[] parts = result.split(" ?\\| ?");
                            if (parts != null && parts.length >= 1)
                                return parts[4];
                        }
                    }
        } catch (final Exception e) {
        }
        return null;
    }

    public static String getAScountry(final long asn) {
        try {
            final Name postfix = Name.fromConstantString("asn.cymru.com.");
            final Name name = new Name(String.format("AS%d", asn), postfix);
            System.out.println("lookup: " + name);

            final Lookup lookup = new Lookup(name, Type.TXT);
            lookup.setResolver(new SimpleResolver());
            lookup.setCache(null);
            final Record[] records = lookup.run();
            if (lookup.getResult() == Lookup.SUCCESSFUL)
                for (final Record record : records)
                    if (record instanceof TXTRecord) {
                        final TXTRecord txt = (TXTRecord) record;
                        @SuppressWarnings("unchecked") final List<String> strings = txt.getStrings();
                        if (strings != null && !strings.isEmpty()) {
                            final String result = strings.get(0);
                            final String[] parts = result.split(" ?\\| ?");
                            if (parts != null && parts.length >= 1)
                                return parts[1];
                        }
                    }
        } catch (final Exception e) {
        }
        return null;
    }

    public static ASInformation getASInformationForSignalRequest(final InetAddress addr) {
        Long asNumber;
        String asName;
        String asCountry;
        var firstServiceTryAsInformation = HelperFunctions.getASInformation(addr);
        if (Objects.nonNull(firstServiceTryAsInformation)) {
            return firstServiceTryAsInformation;
        } else {
            asNumber = HelperFunctions.getASN(addr);
            if (Objects.isNull(asNumber)) {
                asName = null;
                asCountry = null;
            } else {
                asName = HelperFunctions.getASName(asNumber);
                asCountry = HelperFunctions.getAScountry(asNumber);
            }
            return ASInformation.builder()
                    .number(asNumber)
                    .name(asName)
                    .country(asCountry)
                    .build();
        }
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
