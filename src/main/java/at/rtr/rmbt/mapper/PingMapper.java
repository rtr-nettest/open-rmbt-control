package at.rtr.rmbt.mapper;

import at.rtr.rmbt.model.Ping;
import at.rtr.rmbt.model.Test;
import at.rtr.rmbt.request.PingRequest;

/**
 * Ping mapper interface.
 */
public interface PingMapper {

    /**
     * Ping request to ping.
     *
     * @param pingRequest the Ping request
     * @param test the Test
     * @return the result
     */
    Ping pingRequestToPing(PingRequest pingRequest, Test test);
}
