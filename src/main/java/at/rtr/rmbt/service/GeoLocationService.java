package at.rtr.rmbt.service;

import at.rtr.rmbt.model.Test;
import at.rtr.rmbt.request.GeoLocationRequest;
import at.rtr.rmbt.request.ResultUpdateRequest;

import javax.transaction.Transactional;
import java.util.Collection;

public interface GeoLocationService {

    @Transactional(Transactional.TxType.REQUIRES_NEW)
    void processGeoLocationRequests(Collection<GeoLocationRequest> geoLocations, Test test);

    void updateGeoLocation(Test test, ResultUpdateRequest resultUpdateRequest);
}
