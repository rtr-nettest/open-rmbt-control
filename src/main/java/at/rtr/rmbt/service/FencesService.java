package at.rtr.rmbt.service;

import at.rtr.rmbt.model.Test;
import at.rtr.rmbt.request.FencesRequest;
import java.util.Collection;

/**
 * Fences service interface.
 */
public interface FencesService {

    /**
     * Process fences requests.
     *
     * @param fences the Fences
     * @param test the Test
     */
    void processFencesRequests(Collection<FencesRequest> fences, Test test);
}
