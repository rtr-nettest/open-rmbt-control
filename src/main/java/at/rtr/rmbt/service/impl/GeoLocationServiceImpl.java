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

import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class GeoLocationServiceImpl implements GeoLocationService {

    private final GeoLocationMapper geoLocationMapper;
    private final GeoLocationRepository geoLocationRepository;

    @Override
    public void processGeoLocationRequests(Collection<GeoLocationRequest> geoLocationRequests, Test test) {

        Double minAccuracy = Double.MAX_VALUE;
        GeoLocation bestAccuracyPosition = null;   // best (lowest) accuracy among in-test (time_ns >= 0) positions
        GeoLocation firstAccuratePosition = null;  // first in-test position whose accuracy is within the threshold

        // Last-resort fallback (only when NO in-test position exists): newest position recorded within the
        // 10 s pre-start window (time_ns > LOCATION_FALLBACK_MIN_TIME_NS). Selected by time, not accuracy.
        GeoLocation newestPreStartPosition = null;
        Long newestPreStartTimeNs = null;

        List<GeoLocation> geoLocations = new LinkedList<>();

        for (GeoLocationRequest geoDataItem : geoLocationRequests) {
            if (Objects.nonNull(geoDataItem.getTstamp()) &&
                    Objects.nonNull(geoDataItem.getGeoLat()) && Objects.nonNull(geoDataItem.getGeoLong())) {

                GeoLocation geoLoc = geoLocationMapper.geoLocationRequestToGeoLocation(geoDataItem, test);
                geoLocations.add(geoLoc); // every valid position is stored, regardless of its time

                final Long timeNs = geoDataItem.getTimeNs();
                // A position worse than the accuracy limit is ignored entirely for reference selection
                // (all three cases below), so the chosen reference matches the statistics location output.
                final boolean accurateEnough = geoLoc.getAccuracy() < Config.RMBT_GEO_ACCURACY_DETAIL_LIMIT;
                if (accurateEnough && timeNs != null && timeNs >= 0L) {
                    // In-test position (at or after test start): eligible as reference location.
                    if (geoLoc.getAccuracy() < minAccuracy) {
                        minAccuracy = geoLoc.getAccuracy();
                        bestAccuracyPosition = geoLoc;
                    }
                    if (Objects.isNull(firstAccuratePosition) &&
                            geoLoc.getAccuracy() <= Config.LOCATION_ACCURACY_THRESHOLD_M) {
                        firstAccuratePosition = geoLoc;
                    }
                } else if (accurateEnough && timeNs != null && timeNs > Config.LOCATION_FALLBACK_MIN_TIME_NS) {
                    // Pre-start position within the tolerance window: only a candidate for the fallback below.
                    if (newestPreStartTimeNs == null || timeNs > newestPreStartTimeNs) {
                        newestPreStartTimeNs = timeNs;
                        newestPreStartPosition = geoLoc;
                    }
                }
            }
        }
        geoLocationRepository.saveAll(geoLocations);

        // Reference location selection, in order of preference:
        //  1) first in-test position within the accuracy threshold;
        //  2) otherwise the best-accuracy in-test position;
        //  3) only if no in-test position exists at all: the newest position within the 10 s pre-start window.
        final GeoLocation selectedPosition;
        if (Objects.nonNull(firstAccuratePosition)) {
            selectedPosition = firstAccuratePosition;
        } else if (Objects.nonNull(bestAccuracyPosition)) {
            selectedPosition = bestAccuracyPosition;
        } else {
            selectedPosition = newestPreStartPosition; // may be null when nothing qualifies
        }
        if (Objects.nonNull(selectedPosition)) {
            updateTestGeo(test, selectedPosition);
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

    @Override
    public void createAndAssignGeoLocation(Test test, double geoLat, double geoLong, Double geoAccuracy, String provider, ZonedDateTime time) {
        GeoLocation geoLocation = geoLocationMapper.buildNewGeoLocation(test, geoLat, geoLong, geoAccuracy, provider);
        if (time != null) {
            geoLocation.setTime(time);
        }
        geoLocationRepository.save(geoLocation);
        updateTestGeo(test, geoLocation);
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
