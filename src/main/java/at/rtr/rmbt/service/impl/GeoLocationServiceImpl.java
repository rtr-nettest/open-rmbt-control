package at.rtr.rmbt.service.impl;

import at.rtr.rmbt.constant.Config;
import at.rtr.rmbt.mapper.GeoLocationMapper;
import at.rtr.rmbt.model.GeoLocation;
import at.rtr.rmbt.model.Test;
import at.rtr.rmbt.repository.GeoLocationRepository;
import at.rtr.rmbt.request.GeoLocationRequest;
import at.rtr.rmbt.request.ResultUpdateRequest;
import at.rtr.rmbt.service.GeoLocationService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class GeoLocationServiceImpl implements GeoLocationService {

    private final GeoLocationMapper geoLocationMapper;
    private final GeoLocationRepository geoLocationRepository;

    @Override
    public void processGeoLocationRequests(Collection<GeoLocationRequest> geoLocations, Test test) {

        Double minAccuracy = Double.MAX_VALUE;
        GeoLocation firstAccuratePosition = null;

        for (GeoLocationRequest geoDataItem : geoLocations) {
            if (Objects.nonNull(geoDataItem.getTstamp()) && Objects.nonNull(geoDataItem.getGeoLat()) && Objects.nonNull(geoDataItem.getGeoLong())) {
//                if (geoDataItem.getTimeNs() > -20000000000L) {// todo update to another value from RTR branch
                GeoLocation geoLoc = geoLocationMapper.geoLocationRequestToGeoLocation(geoDataItem, test);

                if (geoLoc.getAccuracy() < minAccuracy) {
                    minAccuracy = geoLoc.getAccuracy();
                    firstAccuratePosition = geoLoc;
                }
                GeoLocation savedGeoLocation = geoLocationRepository.save(geoLoc);
                if (savedGeoLocation.getGeoLong() != null && savedGeoLocation.getGeoLat() != null)
                    geoLocationRepository.updateLocation(savedGeoLocation.getId(), savedGeoLocation.getGeoLong(), savedGeoLocation.getGeoLat());
//                }
            }
        }

        if (Objects.nonNull(firstAccuratePosition)) {
            updateTestGeo(test, firstAccuratePosition);
        }
    }

    @Override
    public void updateGeoLocation(Test test, ResultUpdateRequest resultUpdateRequest) {
        final double geoLat = ObjectUtils.defaultIfNull(resultUpdateRequest.getGeoLat(), Double.NaN);
        final double geoLong = ObjectUtils.defaultIfNull(resultUpdateRequest.getGeoLong(), Double.NaN);
        final double geoAccuracy = ObjectUtils.defaultIfNull(resultUpdateRequest.getAccuracy(), NumberUtils.DOUBLE_ZERO);
        final String provider = ObjectUtils.defaultIfNull(resultUpdateRequest.getProvider(), StringUtils.EMPTY).toLowerCase();
        if (isGeoNotNullAndProviderIsSupported(geoLat, geoLong, provider)) {
            GeoLocation geoLocation = geoLocationMapper.buildNewGeoLocation(test, geoLat, geoLong, geoAccuracy, provider);
            geoLocationRepository.save(geoLocation);
            updateTestGeo(test, geoLocation);
        }
    }

    private boolean isGeoNotNullAndProviderIsSupported(double geoLat, double geoLong, String provider) {
        return !Double.isNaN(geoLat) &&
                !Double.isNaN(geoLong) &&
                (provider.equals(Config.GEO_PROVIDER_GEOCODER) ||
                        provider.equals(Config.GEO_PROVIDER_MANUAL) ||
                        provider.equals(Config.GEO_PROVIDER_GPS));
    }

    private void updateTestGeo(Test test, GeoLocation geoLocation) {
        test.setGeoLocationUuid(geoLocation.getGeoLocationUUID());
        test.setGeoAccuracy(geoLocation.getAccuracy());
        test.setLongitude(geoLocation.getGeoLong());
        test.setLatitude(geoLocation.getGeoLat());
        test.setGeoProvider(geoLocation.getProvider());
    }
}
