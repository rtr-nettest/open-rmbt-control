package at.rtr.rmbt.mapper.impl;

import at.rtr.rmbt.config.UUIDGenerator;
import at.rtr.rmbt.constant.Constants;
import at.rtr.rmbt.mapper.GeoLocationMapper;
import at.rtr.rmbt.model.GeoLocation;
import at.rtr.rmbt.model.Test;
import at.rtr.rmbt.request.GeoLocationRequest;
import at.rtr.rmbt.utils.TimeUtils;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.math.NumberUtils;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
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
                .location(getLocationPointFromLongitudeAndLatitude(geoLocationRequest.getGeoLong(), geoLocationRequest.getGeoLat()))
                .mockLocation(geoLocationRequest.isMockLocation())
                .provider(geoLocationRequest.getProvider())
                .speed(geoLocationRequest.getSpeed())
                .timeNs(geoLocationRequest.getTimeNs())
                .time(TimeUtils.getZonedDateTimeFromMillisAndTimezone(geoLocationRequest.getTstamp(), test.getTimezone()))
                .build();
    }

    @Override
    public GeoLocation buildNewGeoLocation(Test test, double geoLat, double geoLong, double geoAccuracy, String provider) {
        return GeoLocation.builder()
                .test(test)
                .time(ZonedDateTime.now())
                .accuracy(geoAccuracy)
                .geoLat(geoLat)
                .geoLong(geoLong)
                .provider(provider)
                .openTestUUID(test.getOpenTestUuid())
                .timeNs(NumberUtils.LONG_ZERO)
                .geoLocationUUID(uuidGenerator.generateUUID())
                .location(getLocationPointFromLongitudeAndLatitude(geoLong, geoLat))
                .build();
    }

    private Point getLocationPointFromLongitudeAndLatitude(Double longitude, Double latitude) {
        return new GeometryFactory(new PrecisionModel(), Constants.SRID)
                .createPoint(new Coordinate(longitude, latitude));
    }
}
