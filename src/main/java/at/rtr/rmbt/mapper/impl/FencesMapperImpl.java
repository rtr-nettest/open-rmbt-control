package at.rtr.rmbt.mapper.impl;

import at.rtr.rmbt.mapper.FencesMapper;
import at.rtr.rmbt.model.Fences;
import at.rtr.rmbt.model.Test;
import at.rtr.rmbt.request.FencesRequest;
import at.rtr.rmbt.utils.GeometryUtils;
import org.locationtech.jts.geom.Geometry;
import org.springframework.stereotype.Service;

@Service
public class FencesMapperImpl implements FencesMapper {

    @Override
    public Fences fencesRequestToFences(FencesRequest fencesRequest, Test test) {
        final double latitude = fencesRequest.getLocation().getLatitude();
        final double longitude = fencesRequest.getLocation().getLongitude();
        final Geometry geom4326 = GeometryUtils.getPointEPSG4326FromLongitudeAndLatitude(longitude, latitude);

        // fenceId and fenceTime are assigned by FencesServiceImpl, which owns the per-test counter.
        return Fences.builder()
                .openTestUUID(test.getOpenTestUuid())
                .technology(fencesRequest.getTechnology())
                .avgPingMs(fencesRequest.getAvgPingMs())
                .technologyId(fencesRequest.getTechnologyId())
                .offsetMs(fencesRequest.getOffsetMs())
                .durationMs(fencesRequest.getDurationMs())
                .radius(fencesRequest.getRadius())
                .geom4326(geom4326)
                .signal(fencesRequest.getSignal())
                // Persist only what the client sent; the 14 m / "gps" defaults apply to the
                // test row's location, not to the stored fence.
                .accuracy(fencesRequest.getAccuracy())
                .provider(fencesRequest.getProvider())
                .build();
    }
}
