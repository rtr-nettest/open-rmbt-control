package at.rtr.rmbt.mapper.impl;

import at.rtr.rmbt.mapper.FencesMapper;
import at.rtr.rmbt.model.Fences;
import at.rtr.rmbt.model.Test;
import at.rtr.rmbt.request.FencesRequest;
import org.springframework.stereotype.Service;

@Service
public class FencesMapperImpl implements FencesMapper {

    @Override
    public Fences fencesRequestToFences(FencesRequest fencesRequest, Test test) {
        // The fence's centre location is no longer stored on the fence; it is held in a geo_location
        // row and referenced via geo_location_uuid (assigned by FencesServiceImpl, which also owns
        // the per-test fenceId/fenceTime counters).
        return Fences.builder()
                .openTestUUID(test.getOpenTestUuid())
                .technology(fencesRequest.getTechnology())
                .avgPingMs(fencesRequest.getAvgPingMs())
                .technologyId(fencesRequest.getTechnologyId())
                .offsetMs(fencesRequest.getOffsetMs())
                .durationMs(fencesRequest.getDurationMs())
                .radius(fencesRequest.getRadius())
                .signal(fencesRequest.getSignal())
                .build();
    }
}
