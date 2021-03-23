package at.rtr.rmbt.service.impl;

import at.rtr.rmbt.mapper.RadioSignalMapper;
import at.rtr.rmbt.model.RadioSignal;
import at.rtr.rmbt.model.Test;
import at.rtr.rmbt.repository.RadioSignalRepository;
import at.rtr.rmbt.request.RadioSignalRequest;
import at.rtr.rmbt.service.RadioSignalService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class RadioSignalServiceImpl implements RadioSignalService {

    private final RadioSignalMapper radioSignalMapper;
    private final RadioSignalRepository radioSignalRepository;

    @Override
    public void saveRadioSignalRequests(Collection<RadioSignalRequest> signals, Test test) {
        int minSignalStrength = Integer.MAX_VALUE; //measured as RSSI (GSM,UMTS,Wifi)
        int minLteRsrp = Integer.MAX_VALUE; //signal strength measured as RSRP
        int minLteRsrq = Integer.MAX_VALUE; //signal quality of LTE measured as RSRQ
        int minLinkSpeed = Integer.MAX_VALUE;
        List<RadioSignal> radioSignals = new ArrayList<>();
        for (RadioSignalRequest signalRequest : signals) {
            RadioSignal newSignal = radioSignalMapper.radioSignalRequestToRadioSignal(signalRequest, test);
            radioSignals.add(newSignal);

            if (Objects.nonNull(newSignal.getSignalStrength()) && newSignal.getSignalStrength() < minSignalStrength) {
                minSignalStrength = newSignal.getSignalStrength();
            }
            if (Objects.nonNull(newSignal.getLteRSRP()) && newSignal.getLteRSRP() < minLteRsrp) {
                minLteRsrp = newSignal.getLteRSRP();
            }

            if (Objects.nonNull(newSignal.getLteRSRQ()) && newSignal.getLteRSRQ() < minLteRsrq) {
                minLteRsrq = newSignal.getLteRSRQ();
            }

            if (Objects.nonNull(newSignal.getWifiLinkSpeed()) && newSignal.getWifiLinkSpeed() < minLinkSpeed) {
                minLinkSpeed = newSignal.getWifiLinkSpeed();
            }
        }
        if (minSignalStrength < 0) { // 0 dBm is out of range
            test.setSignalStrength(minSignalStrength);
        }
        // set rsrp value (typically LTE)
        if (minLteRsrp < 0) { // 0 dBm is out of range
            test.setLteRsrp(minLteRsrp);
        }
        // set rsrq value (LTE)
        if (minLteRsrp < 0) {
            test.setLteRsrq(minLteRsrq);
        }

        if (minLinkSpeed != Integer.MAX_VALUE) {
            test.setWifiLinkSpeed(minLinkSpeed);
        }

        radioSignalRepository.saveAll(radioSignals);
    }
}
