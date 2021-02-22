package at.rtr.rmbt.mapper;

import at.rtr.rmbt.model.GeoLocation;
import at.rtr.rmbt.request.GeoLocationRequest;

public interface GeoLocationMapper {

    GeoLocation geoLocationRequestToGeoLocation(GeoLocationRequest geoLocationRequest);
}
