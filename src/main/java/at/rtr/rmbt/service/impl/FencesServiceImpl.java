package at.rtr.rmbt.service.impl;


import at.rtr.rmbt.mapper.FencesMapper;
import at.rtr.rmbt.model.Fences;
import at.rtr.rmbt.model.Test;
import at.rtr.rmbt.repository.FencesRepository;
import at.rtr.rmbt.request.FencesRequest;
import at.rtr.rmbt.service.FencesService;
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

    @Override
    public void processFencesRequests(Collection<FencesRequest> fences, Test test) {
        final List<Fences> newFences = new ArrayList<>();

        long fenceCount = 0L;
        for (FencesRequest fence : fences) {
            final Fences newFence = fencesMapper.fencesRequestToFences(fence, test);
            // assign the 0-based fence id from the running counter
            newFence.setFenceId(fenceCount);
            // set fenceTime to test timestamp plus offset
            newFence.setFenceTime(test.getTime().plus(fence.getOffsetMs(), ChronoUnit.MILLIS));
            newFences.add(newFence);
            fenceCount++;
        }

        test.setFencesCount(fenceCount);
        fencesRepository.saveAll(newFences);
    }
}
