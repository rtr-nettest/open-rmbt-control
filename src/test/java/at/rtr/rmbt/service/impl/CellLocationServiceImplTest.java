package at.rtr.rmbt.service.impl;

import at.rtr.rmbt.mapper.CellLocationMapper;
import at.rtr.rmbt.model.CellLocation;
import at.rtr.rmbt.repository.CellLocationRepository;
import at.rtr.rmbt.request.CellLocationRequest;
import at.rtr.rmbt.service.CellLocationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
public class CellLocationServiceImplTest {
    private CellLocationService cellLocationService;

    @MockBean
    private CellLocationMapper cellLocationMapper;
    @MockBean
    private CellLocationRepository cellLocationRepository;

    @Mock
    private CellLocationRequest cellLocationRequest;
    @Mock
    private CellLocation cellLocation;
    @Mock
    private at.rtr.rmbt.model.Test test;

    @Before
    public void setUp() {
        cellLocationService = new CellLocationServiceImpl(cellLocationMapper, cellLocationRepository);
    }

    @Test
    public void processCellLocationRequests_whenCommonData_expectSaveCellLocation() {
        var requests = List.of(cellLocationRequest);
        when(cellLocationMapper.cellLocationRequestToCellLocation(cellLocationRequest, test)).thenReturn(cellLocation);

        cellLocationService.saveCellLocationRequests(requests, test);

        verify(cellLocationRepository).saveAll(List.of(cellLocation));
    }
}