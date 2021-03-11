package at.rtr.rmbt.service.impl;

import at.rtr.rmbt.mapper.GeoLocationMapper;
import at.rtr.rmbt.model.GeoLocation;
import at.rtr.rmbt.model.Test;
import at.rtr.rmbt.repository.GeoLocationRepository;
import at.rtr.rmbt.request.GeoLocationRequest;
import at.rtr.rmbt.service.GeoLocationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class GeoLocationServiceImpl implements GeoLocationService {

    private final GeoLocationMapper geoLocationMapper;
    private final GeoLocationRepository geoLocationRepository;

    @Override
    public void processGeoLocationRequests(Collection<GeoLocationRequest> geoLocations, Test test) {
        List<GeoLocation> actualGeoLocation = new ArrayList<>();

        Double minAccuracy = Double.MAX_VALUE;
        GeoLocation firstAccuratePosition = null;

        for (GeoLocationRequest geoDataItem : geoLocations) {
            if (Objects.nonNull(geoDataItem.getTstamp()) && Objects.nonNull(geoDataItem.getGeoLat()) && Objects.nonNull(geoDataItem.getGeoLong())) {
                if (geoDataItem.getTimeNs() > -20000000000L) {// todo update to another value from RTR branch
                    GeoLocation geoLoc = geoLocationMapper.geoLocationRequestToGeoLocation(geoDataItem, test);
                    
                    if (geoLoc.getAccuracy() < minAccuracy) {
                        minAccuracy = geoLoc.getAccuracy();
                        firstAccuratePosition = geoLoc;
                    }
                    actualGeoLocation.add(geoLoc);
                }
            }
        }
        geoLocationRepository.saveAll(actualGeoLocation);

        if (Objects.nonNull(firstAccuratePosition)) {
            updateTestGeo(test, firstAccuratePosition);
        }
    }

    private void updateTestGeo(Test test, GeoLocation firstAccuratePosition) {
        test.setGeoLocationUuid(firstAccuratePosition.getGeoLocationUUID());
        test.setGeoAccuracy(firstAccuratePosition.getAccuracy());
        test.setLongitude(firstAccuratePosition.getGeoLong());
        test.setLatitude(firstAccuratePosition.getGeoLat());
        test.setGeoProvider(firstAccuratePosition.getProvider());
    }
}
