package at.rtr.rmbt.mapper;

import at.rtr.rmbt.model.GeoLocation;
import at.rtr.rmbt.model.Test;
import at.rtr.rmbt.request.GeoLocationRequest;

public interface GeoLocationMapper {

    GeoLocation geoLocationRequestToGeoLocation(GeoLocationRequest geoLocationRequest, Test test);

    GeoLocation buildNewGeoLocation(Test test, double geoLat, double geoLong, double geoAccuracy, String provider);
}
