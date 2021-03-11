package at.rtr.rmbt.service;

import at.rtr.rmbt.model.Test;
import at.rtr.rmbt.request.PingRequest;

import java.util.Collection;

public interface PingService {

    void savePingRequests(Collection<PingRequest> pingRequests, Test test);
}
