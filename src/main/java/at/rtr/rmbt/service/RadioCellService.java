package at.rtr.rmbt.service;

import at.rtr.rmbt.model.Test;
import at.rtr.rmbt.request.RadioCellRequest;

import java.util.Collection;

/**
 * Radio cell service interface.
 */
public interface RadioCellService {

    /**
     * Process radio cell requests.
     *
     * @param cells the Cells
     * @param test the Test
     */
    void processRadioCellRequests(Collection<RadioCellRequest> cells, Test test);
}
