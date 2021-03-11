package at.rtr.rmbt.mapper;

import at.rtr.rmbt.model.Ping;
import at.rtr.rmbt.model.Test;
import at.rtr.rmbt.request.PingRequest;

public interface PingMapper {

    Ping pingRequestToPing(PingRequest pingRequest, Test test);
}
