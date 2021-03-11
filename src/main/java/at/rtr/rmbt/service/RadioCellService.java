package at.rtr.rmbt.service;

import at.rtr.rmbt.model.Test;
import at.rtr.rmbt.request.RadioCellRequest;

import java.util.Collection;

public interface RadioCellService {

    void processRadioCellRequests(Collection<RadioCellRequest> cells, Test test);
}
