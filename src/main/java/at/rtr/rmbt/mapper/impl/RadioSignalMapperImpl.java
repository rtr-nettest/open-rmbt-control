package at.rtr.rmbt.mapper.impl;

import at.rtr.rmbt.config.UUIDGenerator;
import at.rtr.rmbt.mapper.RadioSignalMapper;
import at.rtr.rmbt.model.RadioSignal;
import at.rtr.rmbt.model.Test;
import at.rtr.rmbt.request.RadioSignalRequest;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;

/**
 * Radio signal mapper impl class.
 */
@Service
@RequiredArgsConstructor
public class RadioSignalMapperImpl implements RadioSignalMapper {

    private final UUIDGenerator uuidGenerator;

    /**
     * Radio signal request to radio signal.
     *
     * @param radioSignalRequest the Radio signal request
     * @param test the Test
     * @return the result
     */
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
            .lteRSSNR(ObjectUtils.firstNonNull(radioSignalRequest.getLteRSSNR(), radioSignalRequest.getNrSsSINR()))
            .lteRSRP(ObjectUtils.firstNonNull(radioSignalRequest.getLteRSRP(), radioSignalRequest.getNrSsRSRP()))
            .lteRSRQ(ObjectUtils.firstNonNull(radioSignalRequest.getLteRSRQ(), radioSignalRequest.getNrSsRSRQ()))
            .signalStrength(radioSignalRequest.getSignal())
            .timingAdvance(radioSignalRequest.getTimingAdvance())
            .time(test.getTime().plusNanos(radioSignalRequest.getTimeNs()))
            .timeNs(radioSignalRequest.getTimeNs())
            .timeNsLast(radioSignalRequest.getTimeNsLast())
            .build();
    }
}
