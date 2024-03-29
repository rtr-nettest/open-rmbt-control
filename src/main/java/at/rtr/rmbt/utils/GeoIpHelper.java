package at.rtr.rmbt.utils;

import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.model.CountryResponse;

import java.io.File;
import java.net.InetAddress;

public abstract class GeoIpHelper
{
    private static volatile boolean lookupServiceFailure;
    private static volatile DatabaseReader lookupService;
    private final static Object LOOKUP_SERVICE_LOCK = new Object();

    private static DatabaseReader getLookupService()
    {
        if (lookupService != null) {
            return lookupService;
        }
        synchronized (LOOKUP_SERVICE_LOCK)
        {
            if (lookupServiceFailure) {
                return null;
            }
            // A File object pointing to your GeoIP2 or GeoLite2 database
            File database = new File("/var/lib/GeoIP/GeoLite2-Country.mmdb");
            try
            {
                lookupService = new DatabaseReader.Builder(database).build();
                return lookupService;
            }
            catch (Exception e)
            {
                lookupServiceFailure = true;
                System.out.println("Maxmind GeoIP database could not be loaded");
                return null;
            }
        }
    }


    public static String lookupCountry(final InetAddress adr) {
        try {
            DatabaseReader lookupService = getLookupService();
            if (lookupService != null) {
                CountryResponse country = lookupService.country(adr);
                String countryCode = country.getCountry().getIsoCode();
                return countryCode;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}

