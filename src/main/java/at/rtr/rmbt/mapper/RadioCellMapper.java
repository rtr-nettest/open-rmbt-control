package at.rtr.rmbt.mapper;

import at.rtr.rmbt.model.RadioCell;
import at.rtr.rmbt.model.Test;
import at.rtr.rmbt.request.RadioCellRequest;

/**
 * Radio cell mapper interface.
 */
public interface RadioCellMapper {

    /**
     * Radio cell request to radio cell.
     *
     * @param radioCellRequest the Radio cell request
     * @param test the Test
     * @return the result
     */
    RadioCell radioCellRequestToRadioCell(RadioCellRequest radioCellRequest, Test test);
}
