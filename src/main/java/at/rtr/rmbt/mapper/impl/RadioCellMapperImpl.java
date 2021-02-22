package at.rtr.rmbt.mapper.impl;

import at.rtr.rmbt.mapper.RadioCellMapper;
import at.rtr.rmbt.model.RadioCell;
import at.rtr.rmbt.request.RadioCellRequest;
import org.springframework.stereotype.Service;

@Service
public class RadioCellMapperImpl implements RadioCellMapper {

    @Override
    public RadioCell radioCellRequestToRadioCell(RadioCellRequest rcq) {
        return RadioCell.builder()
            .uuid(rcq.getUuid())
            .mnc(rcq.getMnc())
            .mcc(rcq.getMcc())
            .locationId(rcq.getLocationId())
            .areaCode(rcq.getAreaCode())
            .primaryScramblingCode(rcq.getPrimaryScramblingCode())
            .technology(rcq.getTechnology())
            .channelNumber(rcq.getChannelNumber())
            .registered(rcq.isRegistered())
            .active(rcq.isActive())
            .build();
    }
}
