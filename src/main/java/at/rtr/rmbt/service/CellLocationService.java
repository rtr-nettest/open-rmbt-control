package at.rtr.rmbt.service;

import at.rtr.rmbt.model.Test;
import at.rtr.rmbt.request.CellLocationRequest;

import java.util.Collection;

public interface CellLocationService {

    void saveCellLocationRequests(Collection<CellLocationRequest> cellLocationRequests, Test test);
}
