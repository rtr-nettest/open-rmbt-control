package at.rtr.rmbt.service;

import at.rtr.rmbt.model.Test;
import at.rtr.rmbt.request.RadioSignalRequest;

import java.util.Collection;

public interface RadioSignalService {

    void saveRadioSignalRequests(Collection<RadioSignalRequest> signals, Test test);
}
