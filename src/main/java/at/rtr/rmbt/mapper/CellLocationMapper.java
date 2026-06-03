package at.rtr.rmbt.mapper;

import at.rtr.rmbt.model.CellLocation;
import at.rtr.rmbt.model.Test;
import at.rtr.rmbt.request.CellLocationRequest;

/**
 * Cell location mapper interface.
 */
public interface CellLocationMapper {

    /**
     * Cell location request to cell location.
     *
     * @param cellLocationRequest the Cell location request
     * @param test the Test
     * @return the result
     */
    CellLocation cellLocationRequestToCellLocation(CellLocationRequest cellLocationRequest, Test test);
}
