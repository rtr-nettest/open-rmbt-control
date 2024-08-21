package at.rtr.rmbt.service.impl;

import at.rtr.rmbt.enums.NetworkGroupName;
import at.rtr.rmbt.mapper.RadioCellMapper;
import at.rtr.rmbt.model.RadioCell;
import at.rtr.rmbt.model.Test;
import at.rtr.rmbt.repository.RadioCellRepository;
import at.rtr.rmbt.request.RadioCellRequest;
import at.rtr.rmbt.service.RadioCellService;
import at.rtr.rmbt.utils.BandCalculationUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RadioCellServiceImpl implements RadioCellService {

    private final RadioCellMapper radioCellMapper;
    private final RadioCellRepository radioCellRepository;

    @Override
    public void processRadioCellRequests(Collection<RadioCellRequest> cells, Test test) {
        boolean radioBandChanged = false;
        Integer radioBand = null;
        boolean channelChanged = false;
        Integer channelNumber = null;
        Long locationId = null;
        boolean areaCodeChanged = false;
        Long areaCode = null;
        boolean locationIdChanged = false;

        List<RadioCell> newRadioCells = new ArrayList<>();
        for (RadioCellRequest cell : cells) {
            RadioCell newRadioCell = radioCellMapper.radioCellRequestToRadioCell(cell, test);
            newRadioCells.add(newRadioCell);

            if (newRadioCell.isActive()) {
                if (channelNumber == null) {
                    channelNumber = newRadioCell.getChannelNumber();
                } else if (!channelNumber.equals(newRadioCell.getChannelNumber())) {
                    channelChanged = true;
                }

                if (newRadioCell.getTechnology() != NetworkGroupName.WLAN) {

                    if (locationId == null && !locationIdChanged && newRadioCell.getLocationId() != null) {
                        locationId = newRadioCell.getLocationId();
                    } else {
                        if (!locationIdChanged && locationId != null && newRadioCell.getLocationId() != null &&
                                !locationId.equals(newRadioCell.getLocationId())) {
                            locationIdChanged = true;
                            locationId = null;
                        }
                    }

                    if (areaCode == null && !areaCodeChanged) {
                        areaCode = newRadioCell.getAreaCode();
                    } else if (areaCode != null) {
                        if (!areaCode.equals(newRadioCell.getAreaCode())) {
                            areaCodeChanged = true;
                            areaCode = null;
                        }
                    }

                    if (newRadioCell.getChannelNumber() != null && !radioBandChanged) {

                        BandCalculationUtil.FrequencyInformation fi = BandCalculationUtil.getFrequencyInformationFromChannelNumberAndTechnology(newRadioCell.getChannelNumber(), newRadioCell.getTechnology());

                        if (fi != null) {
                            if (radioBand == null || radioBand.equals(fi.getBand())) {
                                radioBand = fi.getBand();
                            } else {
                                radioBand = null;
                                radioBandChanged = true;
                            }
                        }
                    }
                }
            }
        }
        if (radioBand != null) {
            test.setRadioBand(radioBand);
        }
        if (locationId != null) {
            test.setCellLocationId(locationId);
        }
        if (areaCode != null) {
            test.setCellAreaCode(areaCode);
        }
        if (!channelChanged && channelNumber != null) {
            test.setChannelNumber(channelNumber);
        }

        radioCellRepository.saveAll(newRadioCells);
    }
}
