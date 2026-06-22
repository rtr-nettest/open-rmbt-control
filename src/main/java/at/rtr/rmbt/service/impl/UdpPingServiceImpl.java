package at.rtr.rmbt.service.impl;

import at.rtr.rmbt.mapper.UdpPingMapper;
import at.rtr.rmbt.model.Test;
import at.rtr.rmbt.model.UdpPing;
import at.rtr.rmbt.repository.UdpPingRepository;
import at.rtr.rmbt.request.UdpPingRequest;
import at.rtr.rmbt.service.UdpPingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UdpPingServiceImpl implements UdpPingService {

    private final UdpPingMapper udpPingMapper;
    private final UdpPingRepository udpPingRepository;

    @Override
    public void saveUdpPingRequests(Collection<UdpPingRequest> udpPingRequests, Test test) {
        List<UdpPing> newUdpPings = udpPingRequests.stream()
                .map(udpPingRequest -> udpPingMapper.udpPingRequestToUdpPing(udpPingRequest, test))
                .collect(Collectors.toList());

        udpPingRepository.saveAll(newUdpPings);
    }
}
