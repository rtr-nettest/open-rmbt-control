package at.rtr.rmbt.service.impl;


import at.rtr.rmbt.mapper.FencesMapper;
import at.rtr.rmbt.model.Fences;
import at.rtr.rmbt.model.GeoLocation;
import at.rtr.rmbt.model.Test;
import at.rtr.rmbt.repository.FencesRepository;
import at.rtr.rmbt.request.FencesRequest;
import at.rtr.rmbt.request.SimpleLocationRequest;
import at.rtr.rmbt.service.FencesService;
import at.rtr.rmbt.service.GeoLocationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FencesServiceImpl implements FencesService {

    private final FencesMapper fencesMapper;
    private final FencesRepository fencesRepository;
    private final GeoLocationService geoLocationService;

    @Override
    public void processFencesRequests(Collection<FencesRequest> fences, Test test) {
        final List<Fences> newFences = new ArrayList<>();

        long fenceCount = 0L;
        for (FencesRequest fence : fences) {
            final Fences newFence = fencesMapper.fencesRequestToFences(fence, test);
            // assign the 0-based fence id from the running counter
            newFence.setFenceId(fenceCount);
            // set fenceTime to test timestamp plus offset
            final ZonedDateTime fenceTime = test.getTime().plus(fence.getOffsetMs(), ChronoUnit.MILLIS);
            newFence.setFenceTime(fenceTime);
            // The fence's centre location lives in geo_location; create one per fence and reference
            // it. The first (oldest, index 0) fence's geo_location also defines the test's
            // representative location.
            final GeoLocation geoLocation = createFenceGeoLocation(fence, test, fenceTime, fenceCount == 0L);
            if (geoLocation != null) {
                newFence.setGeoLocation(geoLocation);
            }
            newFences.add(newFence);
            fenceCount++;
        }

        test.setFencesCount(fenceCount);
        fencesRepository.saveAll(newFences);
    }

    private GeoLocation createFenceGeoLocation(FencesRequest fence, Test test, ZonedDateTime fenceTime, boolean assignToTest) {
        final SimpleLocationRequest location = fence.getLocation();
        if (location == null || location.getLatitude() == null || location.getLongitude() == null) {
            return null;
        }
        // accuracy/provider are stored as the client sent them (NULL when absent - no default invented).
        return assignToTest
                ? geoLocationService.createAndAssignGeoLocation(test, location.getLatitude(), location.getLongitude(),
                        fence.getAccuracy(), fence.getProvider(), fenceTime)
                : geoLocationService.createGeoLocation(test, location.getLatitude(), location.getLongitude(),
                        fence.getAccuracy(), fence.getProvider(), fenceTime);
    }
}
