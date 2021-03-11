package at.rtr.rmbt.mapper.impl;

import at.rtr.rmbt.config.UUIDGenerator;
import at.rtr.rmbt.constant.Constants;
import at.rtr.rmbt.mapper.GeoLocationMapper;
import at.rtr.rmbt.model.GeoLocation;
import at.rtr.rmbt.model.Test;
import at.rtr.rmbt.request.GeoLocationRequest;
import at.rtr.rmbt.utils.TimeUtils;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GeoLocationMapperImpl implements GeoLocationMapper {

    private final UUIDGenerator uuidGenerator;

    @Override
    public GeoLocation geoLocationRequestToGeoLocation(GeoLocationRequest geoLocationRequest, Test test) {
        return GeoLocation.builder()
            .test(test)
            .openTestUUID(test.getOpenTestUuid())
            .geoLocationUUID(uuidGenerator.generateUUID())
            .accuracy(Optional.ofNullable(geoLocationRequest.getAccuracy()).orElse(Double.MAX_VALUE))
            .altitude(geoLocationRequest.getAltitude())
            .bearing(geoLocationRequest.getBearing())
            .geoLat(geoLocationRequest.getGeoLat())
            .geoLong(geoLocationRequest.getGeoLong())
            .location(getLocationFromRequest(geoLocationRequest))
            .mockLocation(geoLocationRequest.isMockLocation())
            .provider(geoLocationRequest.getProvider())
            .speed(geoLocationRequest.getSpeed())
            .timeNs(geoLocationRequest.getTimeNs())
            .time(TimeUtils.getZonedDateTimeFromMillisAndTimezone(geoLocationRequest.getTstamp(), test.getTimezone()))
            .build();
    }

    private Point getLocationFromRequest(GeoLocationRequest geoLocationRequest) {
        return new GeometryFactory(new PrecisionModel(), Constants.SRID)
                .createPoint(new Coordinate(geoLocationRequest.getGeoLong(), geoLocationRequest.getGeoLat()));
    }
}
