package at.rtr.rmbt.service;

import at.rtr.rmbt.model.Test;
import at.rtr.rmbt.request.RadioInfoRequest;

/**
 * Radio signal service interface.
 */
public interface RadioSignalService {

    /**
     * Save radio signal requests.
     *
     * @param signals the Signals
     * @param test the Test
     */
    void saveRadioSignalRequests(RadioInfoRequest signals, Test test);
}
