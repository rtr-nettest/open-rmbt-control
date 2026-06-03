package at.rtr.rmbt.service;

import at.rtr.rmbt.model.Test;
import at.rtr.rmbt.request.CellLocationRequest;

import java.util.Collection;

/**
 * Cell location service interface.
 */
public interface CellLocationService {

    /**
     * Save cell location requests.
     *
     * @param cellLocationRequests the Cell location requests
     * @param test the Test
     */
    void saveCellLocationRequests(Collection<CellLocationRequest> cellLocationRequests, Test test);
}
