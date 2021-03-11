package at.rtr.rmbt.service;

import at.rtr.rmbt.model.Test;
import at.rtr.rmbt.request.SpeedDetailsRequest;

import java.util.Collection;

public interface SpeedService {

    void processSpeedRequests(Collection<SpeedDetailsRequest> speedDetailsRequests, Test test);
}
