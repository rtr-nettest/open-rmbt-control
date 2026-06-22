package at.rtr.rmbt.mapper.impl;

import at.rtr.rmbt.mapper.UdpPingMapper;
import at.rtr.rmbt.model.Test;
import at.rtr.rmbt.model.UdpPing;
import at.rtr.rmbt.request.UdpPingRequest;
import org.springframework.stereotype.Service;

@Service
public class UdpPingMapperImpl implements UdpPingMapper {

    @Override
    public UdpPing udpPingRequestToUdpPing(UdpPingRequest udpPingRequest, Test test) {
        return UdpPing.builder()
                .openTestUUID(test.getOpenTestUuid())
                .timeNs(udpPingRequest.getTimeNs())
                .pingMs(udpPingRequest.getValueMs())
                .build();
    }
}
