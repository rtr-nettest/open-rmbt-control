package at.rtr.rmbt.mapper;

import at.rtr.rmbt.model.RadioCell;
import at.rtr.rmbt.request.RadioCellRequest;

public interface RadioCellMapper {

    RadioCell radioCellRequestToRadioCell(RadioCellRequest radioCellRequest);
}
