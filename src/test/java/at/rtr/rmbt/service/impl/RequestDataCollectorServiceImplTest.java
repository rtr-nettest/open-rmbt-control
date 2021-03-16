package at.rtr.rmbt.service.impl;

import at.rtr.rmbt.TestConstants;
import at.rtr.rmbt.service.RequestDataCollectorService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringRunner;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
public class RequestDataCollectorServiceImplTest {
    private RequestDataCollectorService requestDataCollectorService;

    @Mock
    private HttpServletRequest request;
    private final Map<String, String> headers = new HashMap<>();

    @Before
    public void setUp() {
        requestDataCollectorService = new RequestDataCollectorServiceImpl();
        headers.put("User-Agent", TestConstants.DEFAULT_USER_AGENT_STRING);
    }

    @Test
    public void getDataCollectorResponse_whenCommonData_expectDataCollectorResponse() {
        when(request.getRequestURL()).thenReturn(TestConstants.DEFAULT_REQUEST_URL_BUFFER);
        when(request.getLocales()).thenReturn(Collections.enumeration(List.of(Locale.ENGLISH, Locale.FRANCE)));
        when(request.getHeader("User-Agent")).thenReturn(TestConstants.DEFAULT_USER_AGENT_STRING);
        when(request.getRemoteAddr()).thenReturn(TestConstants.DEFAULT_IP);

        var response = requestDataCollectorService.getDataCollectorResponse(request, headers);
        assertEquals(TestConstants.DEFAULT_IP, response.getIp());
        assertEquals(TestConstants.DEFAULT_USER_AGENT_STRING, response.getAgent());
        assertEquals(TestConstants.DEFAULT_REQUEST_URL, response.getUrl());
        assertEquals(List.of(Locale.ENGLISH.toString(), Locale.FRANCE.toString()), response.getLanguages());
        assertEquals(TestConstants.DEFAULT_USER_AGENT_PRODUCT, response.getProduct());
        assertEquals(TestConstants.DEFAULT_USER_AGENT_VERSION, response.getVersion());
        assertEquals(TestConstants.DEFAULT_USER_AGENT_CATEGORY, response.getCategory());
        assertEquals(TestConstants.DEFAULT_USER_AGENT_OS, response.getOs());
        assertEquals(headers, response.getHeaders());
    }
}