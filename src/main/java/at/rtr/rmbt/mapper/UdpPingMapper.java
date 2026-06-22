package at.rtr.rmbt.mapper;

import at.rtr.rmbt.model.Test;
import at.rtr.rmbt.model.UdpPing;
import at.rtr.rmbt.request.UdpPingRequest;

public interface UdpPingMapper {

    UdpPing udpPingRequestToUdpPing(UdpPingRequest udpPingRequest, Test test);
}
