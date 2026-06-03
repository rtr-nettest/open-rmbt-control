package at.rtr.rmbt.utils;

import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.exception.AddressNotFoundException;
import com.maxmind.geoip2.model.CountryResponse;
import com.maxmind.geoip2.model.AsnResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.InetAddress;

/**
 * Geo ip helper class.
 */
public abstract class GeoIpHelper
{
    private static final Logger LOG = LoggerFactory.getLogger(GeoIpHelper.class);

    private static volatile DatabaseReader countryLookupService;
    private static volatile DatabaseReader asnLookupService;

    // Retry control (separate for each DB) ...
    private static volatile long countryNextRetryAtMs;
    private static volatile long asnNextRetryAtMs;

    // ... and retry every 15 minutes after a failure
    private static final long MS_PER_MINUTE = 60 * 1000L;
    private static final long RETRY_DELAY_MS = 15 * MS_PER_MINUTE;

    private static final Object LOOKUP_SERVICE_LOCK = new Object();

    /**
     * Db type enum.
     */
    public enum DbType { COUNTRY, ASN }
    
    // DEFAULT: getCountry() is used: "represents the country where MaxMind believes the end user is located."
    // REGISTERED: getRegisteredCountry() is used: "represents the country where the ISP has registered a given IP block and may differ from the user's country."
    /**
     * Country type enum.
     */
    public enum CountryType { DEFAULT, REGISTERED } 

    private static DatabaseReader getLookupService(final DbType type)
    {
        // Fast path: already initialized
        if (type == DbType.COUNTRY && countryLookupService != null) return countryLookupService;
        if (type == DbType.ASN && asnLookupService != null) return asnLookupService;

        final long now = System.currentTimeMillis();

        // Fast path: not initialized and still in backoff window
        if (type == DbType.COUNTRY && now < countryNextRetryAtMs) return null;
        if (type == DbType.ASN && now < asnNextRetryAtMs) return null;

        synchronized (LOOKUP_SERVICE_LOCK)
        {
            // Re-check after acquiring lock (another thread may have initialized)
            if (type == DbType.COUNTRY && countryLookupService != null) return countryLookupService;
            if (type == DbType.ASN && asnLookupService != null) return asnLookupService;

            // Re-check retry window under lock (another thread may have set it)
            final long nowLocked = System.currentTimeMillis();
            if (type == DbType.COUNTRY && nowLocked < countryNextRetryAtMs) return null;
            if (type == DbType.ASN && nowLocked < asnNextRetryAtMs) return null;

            try {
                switch (type) {
                    case COUNTRY: {
                        File countryDb = new File("/var/lib/GeoIP/GeoLite2-Country.mmdb");
                        countryLookupService = new DatabaseReader.Builder(countryDb).build();
                        countryNextRetryAtMs = 0L; // success -> allow normal operation
                        return countryLookupService;
                    }
                    case ASN: {
                        File asnDb = new File("/var/lib/GeoIP/GeoLite2-ASN.mmdb");
                        asnLookupService = new DatabaseReader.Builder(asnDb).build();
                        asnNextRetryAtMs = 0L; // success
                        return asnLookupService;
                    }
                    default:
                        return null;
                }
            } catch (Exception e) {
                // set next retry time only for the failing DB
                if (type == DbType.COUNTRY) {
                    countryNextRetryAtMs = nowLocked + RETRY_DELAY_MS;
                    System.out.println("Maxmind GeoIP COUNTRY database could not be loaded; retry after " + (RETRY_DELAY_MS / MS_PER_MINUTE) + " minutes");
                } else {
                    asnNextRetryAtMs = nowLocked + RETRY_DELAY_MS;
                    System.out.println("Maxmind GeoIP ASN database could not be loaded; retry after " + (RETRY_DELAY_MS / MS_PER_MINUTE) + " minutes");
                }
                return null;
            }
        }
    }

    /**
     * Lookup country.
     *
     * @param adr the Adr
     * @return the result
     */
    public static String lookupCountry(final InetAddress adr) {
        return lookupCountry(adr, CountryType.DEFAULT);
    }

    /**
     * Lookup country.
     *
     * @param adr the Adr
     * @param type the Type
     * @return the result
     */
    public static String lookupCountry(final InetAddress adr, final CountryType type) {
        try {
            final DatabaseReader lookupService = getLookupService(DbType.COUNTRY);
            if (lookupService == null) {
                return null;
            }
        
            final CountryResponse country = lookupService.country(adr);

            // If called with CountryType.REGISTERED -> return registered country, otherwise (default) the country based on GeoIP
            return (type == CountryType.REGISTERED)
                ? country.getRegisteredCountry().getIsoCode()
                : country.getCountry().getIsoCode();

        } catch (AddressNotFoundException e) {
            // Normal: the address is simply not in the GeoIP database (e.g. private/unknown IP).
            LOG.warn("Country lookup: address {} not found in the GeoIP database", adr.getHostAddress());
            return null;
        } catch (Exception e) {
            LOG.error("Country lookup failed for {}", adr.getHostAddress(), e);
            return null;
        }
    }

    /**
     * Asn info class.
     */
    public static class AsnInfo {
        public final Long autonomousSystemNumber;
        public final String autonomousSystemOrganization;

        /**
         * Creates a new AsnInfo instance.
         *
         * @param autonomousSystemNumber the Autonomous system number
         * @param autonomousSystemOrganization the Autonomous system organization
         */
        public AsnInfo(final Long autonomousSystemNumber, final String autonomousSystemOrganization) {
            this.autonomousSystemNumber = autonomousSystemNumber;
            this.autonomousSystemOrganization = autonomousSystemOrganization;
        }
    }

    /**
     * Lookup asn.
     *
     * @param adr the Adr
     * @return the result
     */
    public static AsnInfo lookupAsn(final InetAddress adr) {
        try {
            final DatabaseReader lookupService = getLookupService(DbType.ASN);
            if (lookupService != null) {
                final AsnResponse asn = lookupService.asn(adr);
                return new AsnInfo(asn.getAutonomousSystemNumber(), asn.getAutonomousSystemOrganization());
            }
        } catch (AddressNotFoundException e) {
            // Normal: the address is simply not in the GeoIP database (e.g. private/unknown IP).
            LOG.warn("ASN lookup: address {} not found in the GeoIP database", adr.getHostAddress());
        } catch (Exception e) {
            LOG.error("ASN lookup failed for {}", adr.getHostAddress(), e);
        }
        return null;
    }

}