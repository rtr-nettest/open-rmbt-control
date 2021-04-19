package at.rtr.rmbt.service;

import at.rtr.rmbt.model.Test;
import at.rtr.rmbt.request.GeoLocationRequest;
import at.rtr.rmbt.request.ResultUpdateRequest;

import java.util.Collection;

public interface GeoLocationService {

    void processGeoLocationRequests(Collection<GeoLocationRequest> geoLocations, Test test);

    void updateGeoLocation(Test test, ResultUpdateRequest resultUpdateRequest);
}
