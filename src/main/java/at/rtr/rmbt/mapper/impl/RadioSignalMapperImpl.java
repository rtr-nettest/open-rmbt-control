package at.rtr.rmbt.mapper.impl;

import at.rtr.rmbt.config.UUIDGenerator;
import at.rtr.rmbt.mapper.RadioSignalMapper;
import at.rtr.rmbt.model.RadioSignal;
import at.rtr.rmbt.model.Test;
import at.rtr.rmbt.request.RadioSignalRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RadioSignalMapperImpl implements RadioSignalMapper {

    private final UUIDGenerator uuidGenerator;

    @Override
    public RadioSignal radioSignalRequestToRadioSignal(RadioSignalRequest radioSignalRequest, Test test) {
        return RadioSignal.builder()
            .openTestUUID(test.getOpenTestUuid())
            .radioSignalUUID(uuidGenerator.generateUUID())
            .cellUUID(radioSignalRequest.getCellUUID())
            .networkTypeId(radioSignalRequest.getNetworkTypeId())
            .bitErrorRate(radioSignalRequest.getBitErrorRate())
            .wifiLinkSpeed(radioSignalRequest.getWifiLinkSpeed())
            .lteCQI(radioSignalRequest.getLteCQI())
            .lteRSSNR(radioSignalRequest.getLteRSSNR())
            .lteRSRP(radioSignalRequest.getLteRSRP())
            .lteRSRQ(radioSignalRequest.getLteRSRQ())
            .signalStrength(radioSignalRequest.getSignal())
            .timingAdvance(radioSignalRequest.getTimingAdvance())
            .timeNs(radioSignalRequest.getTimeNs())
            .timeNsLast(radioSignalRequest.getTimeNsLast())
            .build();
    }
}
