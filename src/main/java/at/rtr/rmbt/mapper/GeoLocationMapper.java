package at.rtr.rmbt.mapper;

import at.rtr.rmbt.model.GeoLocation;
import at.rtr.rmbt.model.Test;
import at.rtr.rmbt.request.GeoLocationRequest;

/**
 * Geo location mapper interface.
 */
public interface GeoLocationMapper {

    /**
     * Geo location request to geo location.
     *
     * @param geoLocationRequest the Geo location request
     * @param test the Test
     * @return the result
     */
    GeoLocation geoLocationRequestToGeoLocation(GeoLocationRequest geoLocationRequest, Test test);

    /**
     * Build new geo location.
     *
     * @param test the Test
     * @param geoLat the Geo lat
     * @param geoLong the Geo long
     * @param geoAccuracy the Geo accuracy
     * @param provider the Provider
     * @return the result
     */
    GeoLocation buildNewGeoLocation(Test test, double geoLat, double geoLong, double geoAccuracy, String provider);
}
