package at.rtr.rmbt.service.impl;

import at.rtr.rmbt.mapper.CellLocationMapper;
import at.rtr.rmbt.model.CellLocation;
import at.rtr.rmbt.model.Test;
import at.rtr.rmbt.repository.CellLocationRepository;
import at.rtr.rmbt.request.CellLocationRequest;
import at.rtr.rmbt.service.CellLocationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CellLocationServiceImpl implements CellLocationService {

    private final CellLocationMapper cellLocationMapper;
    private final CellLocationRepository cellLocationRepository;

    @Override
    public void saveCellLocationRequests(Collection<CellLocationRequest> cellLocationRequests, Test test) {
        List<CellLocation> newCellLocation = cellLocationRequests.stream()
                .map(cellLocationRequest -> cellLocationMapper.cellLocationRequestToCellLocation(cellLocationRequest, test))
                .collect(Collectors.toList());

        cellLocationRepository.saveAll(newCellLocation);
    }
}
