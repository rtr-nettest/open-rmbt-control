package at.rtr.rmbt.mapper;

import at.rtr.rmbt.model.RadioSignal;
import at.rtr.rmbt.model.Test;
import at.rtr.rmbt.request.RadioSignalRequest;

/**
 * Radio signal mapper interface.
 */
public interface RadioSignalMapper {

    /**
     * Radio signal request to radio signal.
     *
     * @param radioSignalRequest the Radio signal request
     * @param test the Test
     * @return the result
     */
    RadioSignal radioSignalRequestToRadioSignal(RadioSignalRequest radioSignalRequest, Test test);
}
