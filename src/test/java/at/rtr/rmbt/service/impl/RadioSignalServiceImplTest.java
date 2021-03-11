package at.rtr.rmbt.service.impl;

import at.rtr.rmbt.mapper.RadioSignalMapper;
import at.rtr.rmbt.model.RadioSignal;
import at.rtr.rmbt.repository.RadioSignalRepository;
import at.rtr.rmbt.request.RadioSignalRequest;
import at.rtr.rmbt.service.RadioSignalService;
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
public class RadioSignalServiceImplTest {
    private RadioSignalService radioSignalService;

    @MockBean
    private RadioSignalMapper radioSignalMapper;
    @MockBean
    private RadioSignalRepository radioSignalRepository;

    @Mock
    private RadioSignalRequest radioSignalRequest;
    @Mock
    private RadioSignal radioSignal;
    @Mock
    private at.rtr.rmbt.model.Test test;

    @Before
    public void setUp() {
        radioSignalService = new RadioSignalServiceImpl(radioSignalMapper, radioSignalRepository);
    }

    @Test
    public void processRadioSignalRequests_whenCommonData_expectRadioSignalSaved() {
        var requests = List.of(radioSignalRequest);
        when(radioSignalMapper.radioSignalRequestToRadioSignal(radioSignalRequest, test)).thenReturn(radioSignal);

        radioSignalService.saveRadioSignalRequests(requests, test);

        verify(radioSignalRepository).saveAll(List.of(radioSignal));
    }
}