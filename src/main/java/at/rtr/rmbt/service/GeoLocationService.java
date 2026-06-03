package at.rtr.rmbt.service;

import at.rtr.rmbt.model.Test;
import at.rtr.rmbt.request.GeoLocationRequest;
import at.rtr.rmbt.request.ResultUpdateRequest;

import jakarta.transaction.Transactional;
import java.util.Collection;

/**
 * Geo location service interface.
 */
public interface GeoLocationService {

    /**
     * Process geo location requests.
     *
     * @param geoLocations the Geo locations
     * @param test the Test
     */
    @Transactional(Transactional.TxType.REQUIRES_NEW)
    void processGeoLocationRequests(Collection<GeoLocationRequest> geoLocations, Test test);

    /**
     * Update geo location.
     *
     * @param test the Test
     * @param resultUpdateRequest the Result update request
     */
    void updateGeoLocation(Test test, ResultUpdateRequest resultUpdateRequest);
}
