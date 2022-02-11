package at.rtr.rmbt.service;

import at.rtr.rmbt.model.Test;
import at.rtr.rmbt.request.RadioInfoRequest;

public interface RadioSignalService {

    void saveRadioSignalRequests(RadioInfoRequest signals, Test test);
}
