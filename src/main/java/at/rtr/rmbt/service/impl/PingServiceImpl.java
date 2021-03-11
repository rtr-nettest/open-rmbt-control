package at.rtr.rmbt.service.impl;

import at.rtr.rmbt.mapper.PingMapper;
import at.rtr.rmbt.model.Ping;
import at.rtr.rmbt.model.Test;
import at.rtr.rmbt.repository.PingRepository;
import at.rtr.rmbt.request.PingRequest;
import at.rtr.rmbt.service.PingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PingServiceImpl implements PingService {

    private final PingMapper pingMapper;
    private final PingRepository pingRepository;

    @Override
    public void savePingRequests(Collection<PingRequest> pingRequests, Test test) {
        List<Ping> newPings = pingRequests.stream()
                .map(pingRequest -> pingMapper.pingRequestToPing(pingRequest, test))
                .collect(Collectors.toList());

        pingRepository.saveAll(newPings);
    }
}
