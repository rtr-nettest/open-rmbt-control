package at.rtr.rmbt.service.impl;

import at.rtr.rmbt.mapper.PingMapper;
import at.rtr.rmbt.model.Ping;
import at.rtr.rmbt.repository.PingRepository;
import at.rtr.rmbt.request.PingRequest;
import at.rtr.rmbt.service.PingService;
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
public class PingServiceImplTest {
    private PingService pingService;

    @MockBean
    private PingMapper pingMapper;
    @MockBean
    private PingRepository pingRepository;

    @Mock
    private at.rtr.rmbt.model.Test test;
    @Mock
    private PingRequest pingRequest;
    @Mock
    private Ping ping;

    @Before
    public void setUp() {
        pingService = new PingServiceImpl(pingMapper, pingRepository);
    }

    @Test
    public void processPingRequests_whenCommonData_expectPingSaved() {
        var requests = List.of(pingRequest);
        when(pingMapper.pingRequestToPing(pingRequest, test)).thenReturn(ping);

        pingService.savePingRequests(requests, test);

        verify(pingRepository).saveAll(List.of(ping));
    }
}