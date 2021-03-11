package at.rtr.rmbt.mapper.impl;

import at.rtr.rmbt.mapper.PingMapper;
import at.rtr.rmbt.model.Ping;
import at.rtr.rmbt.model.Test;
import at.rtr.rmbt.request.PingRequest;
import org.springframework.stereotype.Service;

@Service
public class PingMapperImpl implements PingMapper {

    @Override
    public Ping pingRequestToPing(PingRequest pingRequest, Test test) {
        return Ping.builder()
                .openTestUUID(test.getOpenTestUuid())
                .test(test)
                .value(pingRequest.getValue())
                .valueServer(pingRequest.getValueServer())
                .timeNs(pingRequest.getTimeNs())
                .build();
    }
}
