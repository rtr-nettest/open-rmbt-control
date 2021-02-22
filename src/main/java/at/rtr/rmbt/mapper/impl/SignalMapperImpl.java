package at.rtr.rmbt.mapper.impl;

import at.rtr.rmbt.enums.NetworkGroupName;
import at.rtr.rmbt.mapper.SignalMapper;
import at.rtr.rmbt.model.RadioCell;
import at.rtr.rmbt.model.Test;
import at.rtr.rmbt.response.SignalMeasurementResponse;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class SignalMapperImpl implements SignalMapper {
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

    private String getTechnology(Test test) {
        return test.getRadioCell().stream()
                .map(RadioCell::getTechnology)
                .filter(Objects::nonNull)
                .map(NetworkGroupName::getLabelEn)
                .findFirst()
                .orElse(null);
    }
}
