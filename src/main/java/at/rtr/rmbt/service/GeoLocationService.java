package at.rtr.rmbt.service;

import at.rtr.rmbt.model.Test;
import at.rtr.rmbt.request.GeoLocationRequest;

import java.util.Collection;

public interface GeoLocationService {

    void processGeoLocationRequests(Collection<GeoLocationRequest> geoLocations, Test test);
}
