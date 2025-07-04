package at.rtr.rmbt.service.impl;


import at.rtr.rmbt.mapper.FencesMapper;
import at.rtr.rmbt.model.Fences;
import at.rtr.rmbt.model.Test;
import at.rtr.rmbt.repository.FencesRepository;
import at.rtr.rmbt.request.FencesRequest;
import at.rtr.rmbt.service.FencesService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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


        List<Fences> newFences = new ArrayList<>();

        test.setFencesCount(1L);
        for (FencesRequest fence : fences) {
            // increase fences count with each fence
            Fences newFence = fencesMapper.fencesRequestToFences(fence, test);
            test.setFencesCount(test.getFencesCount() + 1L);
            newFences.add(newFence);

        }

        fencesRepository.saveAll(newFences);
    }
}
