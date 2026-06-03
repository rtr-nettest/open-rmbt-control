package at.rtr.rmbt.service;

import at.rtr.rmbt.model.Test;
import at.rtr.rmbt.request.PingRequest;

import java.util.Collection;

/**
 * Ping service interface.
 */
public interface PingService {

    /**
     * Save ping requests.
     *
     * @param pingRequests the Ping requests
     * @param test the Test
     */
    void savePingRequests(Collection<PingRequest> pingRequests, Test test);
}
