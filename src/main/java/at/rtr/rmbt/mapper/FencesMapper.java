package at.rtr.rmbt.mapper;

import at.rtr.rmbt.model.Fences;
import at.rtr.rmbt.model.Test;
import at.rtr.rmbt.request.FencesRequest;


/**
 * Fences mapper interface.
 */
public interface FencesMapper {

    /**
     * Fences request to fences.
     *
     * @param fr the Fr
     * @param test the Test
     * @return the result
     */
    Fences fencesRequestToFences(Fences fr, Test test);

    /**
     * Fences request to fences.
     *
     * @param fencesRequest the Fences request
     * @param test the Test
     * @return the result
     */
    Fences  fencesRequestToFences(FencesRequest fencesRequest, Test test);
}
