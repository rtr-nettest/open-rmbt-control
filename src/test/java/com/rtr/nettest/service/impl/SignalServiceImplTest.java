package com.rtr.nettest.service.impl;

import com.rtr.nettest.constant.HeaderConstants;
import com.rtr.nettest.model.RtrClient;
import com.rtr.nettest.repository.ClientRepository;
import com.rtr.nettest.repository.RTRProviderRepository;
import com.rtr.nettest.repository.TestRepository;
import com.rtr.nettest.request.SignalRequest;
import com.rtr.nettest.response.SignalResponse;
import com.rtr.nettest.service.SignalService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

import static com.rtr.nettest.TestConstants.*;
import static com.rtr.nettest.constant.URIConstants.SIGNAL_RESULT;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
public class SignalServiceImplTest {
    private SignalService signalService;

    @MockBean
    private TestRepository testRepository;
    @MockBean
    private RTRProviderRepository providerRepository;
    @MockBean
    private UUIDGenerator uuidGenerator;
    @MockBean
    private ClientRepository clientRepository;

    @Mock
    private SignalRequest signalRequest;
    @Mock
    private HttpServletRequest httpServletRequest;
    @Mock
    private com.rtr.nettest.model.Test savedTest;
    @Mock
    private RtrClient rtrClient;

    @Before
    public void setUp() {
        signalService = new SignalServiceImpl(testRepository, providerRepository, uuidGenerator, clientRepository);
    }

    @Test
    public void registerSignal_whenCommonRequest_expectSignalResponse() {
        var expectedResponse = getRegisterSignalResponse();
        when(httpServletRequest.getRemoteAddr()).thenReturn(DEFAULT_IP);
        when(httpServletRequest.getHeader(HeaderConstants.URL)).thenReturn(DEFAULT_URL);
        when(signalRequest.getUuid()).thenReturn(DEFAULT_CLIENT_UUID);
        when(signalRequest.getTimezone()).thenReturn(DEFAULT_TIMEZONE);
        when(clientRepository.findByUuid(DEFAULT_CLIENT_UUID)).thenReturn(Optional.of(rtrClient));
        when(providerRepository.getProviderNameByTestId(DEFAULT_UID)).thenReturn(DEFAULT_PROVIDER);
        when(testRepository.save(any())).thenReturn(savedTest);
        when(savedTest.getId()).thenReturn(DEFAULT_UID);
        when(savedTest.getUuid()).thenReturn(DEFAULT_UUID);

        var actualResponse = signalService.registerSignal(signalRequest, httpServletRequest);

        assertEquals(expectedResponse, actualResponse);
    }

    private SignalResponse getRegisterSignalResponse() {
        return SignalResponse.builder()
                .resultUrl(String.join(DEFAULT_URL, SIGNAL_RESULT))
                .clientRemoteIp(DEFAULT_IP)
                .provider(DEFAULT_PROVIDER)
                .testUUID(DEFAULT_UUID)
                .build();
    }
}