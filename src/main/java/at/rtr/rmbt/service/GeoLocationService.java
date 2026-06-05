package at.rtr.rmbt.service;

import at.rtr.rmbt.model.GeoLocation;
import at.rtr.rmbt.model.Test;
import at.rtr.rmbt.request.GeoLocationRequest;
import at.rtr.rmbt.request.ResultUpdateRequest;

import jakarta.transaction.Transactional;
import java.time.ZonedDateTime;
import java.util.Collection;

public interface GeoLocationService {

    @Transactional(Transactional.TxType.REQUIRES_NEW)
    void processGeoLocationRequests(Collection<GeoLocationRequest> geoLocations, Test test);

    void updateGeoLocation(Test test, ResultUpdateRequest resultUpdateRequest);

    /**
     * Creates and persists a single {@code geo_location} row with a server-generated UUID from the
     * given coordinates and returns it. {@code geoAccuracy} and {@code provider} are stored as given
     * (NULL when the client did not supply them — no default is invented). When {@code time} is
     * non-null it is used as the geo_location timestamp; otherwise the current time is kept.
     * Used for fence locations, which have no client geo_location of their own.
     */
    GeoLocation createGeoLocation(Test test, double geoLat, double geoLong, Double geoAccuracy, String provider, ZonedDateTime time);

    /**
     * Like {@link #createGeoLocation} but additionally assigns the created geo_location to the test
     * (sets {@code geo_location_uuid}, lat/long, accuracy and provider on the test row).
     */
    GeoLocation createAndAssignGeoLocation(Test test, double geoLat, double geoLong, Double geoAccuracy, String provider, ZonedDateTime time);
}
