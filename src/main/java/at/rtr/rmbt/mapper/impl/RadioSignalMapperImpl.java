package at.rtr.rmbt.mapper.impl;

import at.rtr.rmbt.mapper.RadioSignalMapper;
import at.rtr.rmbt.model.RadioSignal;
import at.rtr.rmbt.request.RadioSignalRequest;
import org.springframework.stereotype.Service;

@Service
public class RadioSignalMapperImpl implements RadioSignalMapper {

    @Override
    public RadioSignal radioSignalRequestToRadioSignal(RadioSignalRequest radioSignalRequest) {
        return RadioSignal.builder()
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
