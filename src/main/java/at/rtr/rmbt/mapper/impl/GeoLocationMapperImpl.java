package at.rtr.rmbt.mapper.impl;

import at.rtr.rmbt.config.UUIDGenerator;
import at.rtr.rmbt.mapper.GeoLocationMapper;
import at.rtr.rmbt.model.GeoLocation;
import at.rtr.rmbt.request.GeoLocationRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GeoLocationMapperImpl implements GeoLocationMapper {

    private final UUIDGenerator uuidGenerator;

    @Override
    public GeoLocation geoLocationRequestToGeoLocation(GeoLocationRequest geoLocationRequest) {
        return GeoLocation.builder()
            .geoLocationUUID(uuidGenerator.generateUUID())
            .accuracy(Optional.ofNullable(geoLocationRequest.getAccuracy()).orElse(Double.MAX_VALUE))
            .altitude(geoLocationRequest.getAltitude())
            .bearing(geoLocationRequest.getBearing())
            .geoLat(geoLocationRequest.getGeoLat())
            .geoLong(geoLocationRequest.getGeoLong())
            .mockLocation(geoLocationRequest.isMockLocation())
            .provider(geoLocationRequest.getProvider())
            .speed(geoLocationRequest.getSpeed())
            .timeNs(geoLocationRequest.getTimeNs())
            .build();
    }
}
