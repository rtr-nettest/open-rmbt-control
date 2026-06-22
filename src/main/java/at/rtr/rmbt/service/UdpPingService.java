package at.rtr.rmbt.service;

import at.rtr.rmbt.model.Test;
import at.rtr.rmbt.request.UdpPingRequest;

import java.util.Collection;

public interface UdpPingService {

    void saveUdpPingRequests(Collection<UdpPingRequest> udpPingRequests, Test test);
}
