package at.rtr.rmbt.mapper;

import at.rtr.rmbt.model.RadioSignal;
import at.rtr.rmbt.request.RadioSignalRequest;

public interface RadioSignalMapper {

    RadioSignal radioSignalRequestToRadioSignal(RadioSignalRequest radioSignalRequest);
}
