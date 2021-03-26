package at.rtr.rmbt.mapper.impl;

import at.rtr.rmbt.config.UUIDGenerator;
import at.rtr.rmbt.enums.MeasurementType;
import at.rtr.rmbt.mapper.SignalMapper;
import at.rtr.rmbt.model.RadioCell;
import at.rtr.rmbt.model.Signal;
import at.rtr.rmbt.model.Test;
import at.rtr.rmbt.repository.GeoLocationRepository;
import at.rtr.rmbt.repository.RadioSignalRepository;
import at.rtr.rmbt.request.SignalRequest;
import at.rtr.rmbt.response.SignalMeasurementResponse;
import at.rtr.rmbt.utils.HelperFunctions;
import at.rtr.rmbt.utils.TimeUtils;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SignalMapperImpl implements SignalMapper {

    private final UUIDGenerator uuidGenerator;
    private final RadioSignalRepository radioSignalRepository;
    private final GeoLocationRepository geoLocationRepository;

    @Override
    public SignalMeasurementResponse signalToSignalMeasurementResponse(Test test) {
        List<UUID> radioCellUUIDs = test.getRadioCell().stream()
                .map(RadioCell::getUuid)
                .collect(Collectors.toList());
        return SignalMeasurementResponse.builder()
                .testUuid(test.getUuid())
                .userUuid(test.getClient().getUuid())
                .technology(getTechnology(radioCellUUIDs))
                .testType(test.getMeasurementType() == null ? MeasurementType.DEDICATED.getValueEn() : test.getMeasurementType().getValueEn())
                .location(test.getLocation())
                .duration(calculateDuration(test, radioCellUUIDs))
                .time(test.getTime())
                .build();
    }

    private Long calculateDuration(Test test, List<UUID> radioCellUUIDs) {
        long lastRadioSignalTimeNs = radioSignalRepository.findMaxByCellUUIDIn(radioCellUUIDs)
                .orElse(NumberUtils.LONG_ZERO);
        long lastGeoLocationTimeNs = geoLocationRepository.findMaxByTest(test)
                .orElse(NumberUtils.LONG_ZERO);
        return TimeUtils.formatToSecondsRound(Long.max(lastRadioSignalTimeNs, lastGeoLocationTimeNs));
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
                .time(TimeUtils.getZonedDateTimeFromMillisAndTimezone(signalRequest.getTime(), test.getTimezone()))
                .timeNs(signalRequest.getTimeNs())
                .wifiLinkSpeed(signalRequest.getWifiLinkSpeed())
                .wifiRSSI(signalRequest.getWifiRSSI())
                .build();
    }

    private String getTechnology(List<UUID> radioCellUUIDs) {
        return radioSignalRepository.findDistinctNetworkTypeIdByCellUUIDIn(radioCellUUIDs)
                .stream()
                .filter(Objects::nonNull)
                .map(HelperFunctions::getNetworkTypeName)
                .collect(Collectors.joining(" + "));
    }
}
