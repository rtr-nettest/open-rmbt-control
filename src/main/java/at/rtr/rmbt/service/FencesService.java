package at.rtr.rmbt.service;

import at.rtr.rmbt.model.Test;
import at.rtr.rmbt.request.FencesRequest;
import java.util.Collection;

public interface FencesService {

    void processFencesRequests(Collection<FencesRequest> fences, Test test);
}
