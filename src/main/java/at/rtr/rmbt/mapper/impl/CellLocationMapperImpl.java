package at.rtr.rmbt.mapper.impl;

import at.rtr.rmbt.mapper.CellLocationMapper;
import at.rtr.rmbt.model.CellLocation;
import at.rtr.rmbt.model.Test;
import at.rtr.rmbt.request.CellLocationRequest;
import at.rtr.rmbt.utils.TimeUtils;
import org.springframework.stereotype.Service;

@Service
public class CellLocationMapperImpl implements CellLocationMapper {

    @Override
    public CellLocation cellLocationRequestToCellLocation(CellLocationRequest cellLocationRequest, Test test) {
        return CellLocation.builder()
                .test(test)
                .openTestUUID(test.getOpenTestUuid())
                .areaCode(cellLocationRequest.getAreaCode())
                .locationId(cellLocationRequest.getLocationId())
                .primaryScramblingCode(cellLocationRequest.getPrimaryScramblingCode())
                .time(TimeUtils.getZonedDateTimeFromMillisAndTimezone(cellLocationRequest.getTime(), test.getTimezone()))
                .timeNs(cellLocationRequest.getTimeNs())
                .build();
    }
}
