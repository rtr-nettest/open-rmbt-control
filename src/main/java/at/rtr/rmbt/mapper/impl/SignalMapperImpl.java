package at.rtr.rmbt.mapper.impl;

import at.rtr.rmbt.config.UUIDGenerator;
import at.rtr.rmbt.enums.NetworkGroupName;
import at.rtr.rmbt.mapper.SignalMapper;
import at.rtr.rmbt.model.RadioCell;
import at.rtr.rmbt.model.Signal;
import at.rtr.rmbt.model.Test;
import at.rtr.rmbt.request.SignalRequest;
import at.rtr.rmbt.response.SignalMeasurementResponse;
import at.rtr.rmbt.utils.TimeUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class SignalMapperImpl implements SignalMapper {

    private final UUIDGenerator uuidGenerator;

    @Override
    public SignalMeasurementResponse signalToSignalMeasurementResponse(Test test) {
        return SignalMeasurementResponse.builder()
                .testUuid(test.getUuid())
                .userUuid(test.getClient().getUuid())
                .technology(test.getNetworkGroupName() == null ? getTechnology(test) : test.getNetworkGroupName().getLabelEn())
                .testType(Objects.isNull(test.getLoopModeSettings()) ? "Regular" : "Loop") //TODO temp because of new dedicated mode
                .location(test.getLocation())
                .duration(test.getDuration())
                .time(test.getTime())
                .build();
    }

    @Override
    public Signal signalRequestToSignal(SignalRequest signalRequest, Test test) {
        return Signal.builder()
                .test(test)
                .openTestUUID(test.getOpenTestUuid())
                .gsmBitErrorRate(signalRequest.getGsmBitErrorRate())
                .lteCQI(signalRequest.getLteCQI())
                .lteRSRP(signalRequest.getLteRSRP())
                .lteRSRQ(signalRequest.getLteRSRQ())
                .lteRSSNR(signalRequest.getLteRSSNR())
                .networkTypeId(signalRequest.getNetworkTypeId())
                .signalStrength(signalRequest.getSignalStrength())
                .signalUUID(uuidGenerator.generateUUID())
                .time(TimeUtils.getZonedDateTimeFromMillisAndTimezone(signalRequest.getTime(),test.getTimezone()))
                .timeNs(signalRequest.getTimeNs())
                .wifiLinkSpeed(signalRequest.getWifiLinkSpeed())
                .wifiRSSI(signalRequest.getWifiRSSI())
                .build();
    }

    private String getTechnology(Test test) {
        return test.getRadioCell().stream()
                .map(RadioCell::getTechnology)
                .filter(Objects::nonNull)
                .map(NetworkGroupName::getLabelEn)
                .findFirst()
                .orElse(null);
    }
}
