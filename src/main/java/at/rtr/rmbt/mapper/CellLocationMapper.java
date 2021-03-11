package at.rtr.rmbt.mapper;

import at.rtr.rmbt.model.CellLocation;
import at.rtr.rmbt.model.Test;
import at.rtr.rmbt.request.CellLocationRequest;

public interface CellLocationMapper {

    CellLocation cellLocationRequestToCellLocation(CellLocationRequest cellLocationRequest, Test test);
}
