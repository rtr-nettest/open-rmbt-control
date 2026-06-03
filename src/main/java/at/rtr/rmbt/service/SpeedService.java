package at.rtr.rmbt.service;

import at.rtr.rmbt.model.Test;
import at.rtr.rmbt.request.SpeedDetailsRequest;

import java.util.Collection;

/**
 * Speed service interface.
 */
public interface SpeedService {

    /**
     * Process speed requests.
     *
     * @param speedDetailsRequests the Speed details requests
     * @param test the Test
     */
    void processSpeedRequests(Collection<SpeedDetailsRequest> speedDetailsRequests, Test test);
}
