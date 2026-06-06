package at.rtr.rmbt.service.impl;

import at.rtr.rmbt.enums.ServerType;
import at.rtr.rmbt.model.TestServer;
import at.rtr.rmbt.model.TestServerQuality;
import at.rtr.rmbt.repository.TestServerQualityRepository;
import at.rtr.rmbt.repository.TestServerRepository;
import at.rtr.rmbt.service.quality.PingOutcome;
import at.rtr.rmbt.service.quality.RmbtPinger;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TestServerQualityServiceTest {

    @Mock
    private TestServerRepository testServerRepository;
    @Mock
    private TestServerQualityRepository testServerQualityRepository;
    @Mock
    private RmbtPinger pinger;

    @InjectMocks
    private TestServerQualityService service;

    private static final UUID SERVER_UUID = UUID.fromString("aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee");

    private static TestServer server(final String v4, final String v6) {
        return TestServer.builder()
                .name("srv")
                .uuid(SERVER_UUID)
                .key("secret")
                .portSsl(443)
                .webAddressIpV4(v4)
                .webAddressIpV6(v6)
                .serverType(ServerType.RMBThttp)
                .active(true)
                .build();
    }

    @Test
    void measureAll_pingsV4AndV6AndPersistsBothOutcomes() {
        when(testServerRepository.findByServerTypeAndActiveTrue(ServerType.RMBThttp))
                .thenReturn(List.of(server("v4.example.com", "v6.example.com")));
        when(pinger.ping(eq("v4.example.com"), eq(443), anyString())).thenReturn(PingOutcome.reachable(12.5));
        when(pinger.ping(eq("v6.example.com"), eq(443), anyString())).thenReturn(PingOutcome.unreachable());

        service.measureAll();

        // Both families pinged on the SSL port with a (non-blank) token.
        verify(pinger).ping(eq("v4.example.com"), eq(443), anyString());
        verify(pinger).ping(eq("v6.example.com"), eq(443), anyString());

        final ArgumentCaptor<TestServerQuality> captor = ArgumentCaptor.forClass(TestServerQuality.class);
        verify(testServerQualityRepository, times(2)).save(captor.capture());
        final Map<Integer, TestServerQuality> byProtocol = captor.getAllValues().stream()
                .collect(Collectors.toMap(TestServerQuality::getProtocol, Function.identity()));

        final TestServerQuality v4 = byProtocol.get(4);
        assertEquals(SERVER_UUID, v4.getServerUuid());
        assertTrue(v4.getReachable());
        assertEquals(12.5, v4.getLatencyMs());
        assertEquals(SERVER_UUID, v4.getServerUuid());

        final TestServerQuality v6 = byProtocol.get(6);
        assertFalse(v6.getReachable());
        assertNull(v6.getLatencyMs());
    }

    @Test
    void measureAll_skipsBlankAddressFamily() {
        when(testServerRepository.findByServerTypeAndActiveTrue(ServerType.RMBThttp))
                .thenReturn(List.of(server("v4.example.com", "   ")));
        when(pinger.ping(eq("v4.example.com"), eq(443), anyString())).thenReturn(PingOutcome.reachable(5.0));

        service.measureAll();

        verify(pinger, times(1)).ping(anyString(), anyInt(), anyString());
        verify(pinger, never()).ping(eq("   "), anyInt(), anyString());
        verify(testServerQualityRepository, times(1)).save(org.mockito.ArgumentMatchers.any());
    }

    @Test
    void measureAll_recordsUnreachableWhenPingerThrows() {
        when(testServerRepository.findByServerTypeAndActiveTrue(ServerType.RMBThttp))
                .thenReturn(List.of(server("v4.example.com", null)));
        when(pinger.ping(eq("v4.example.com"), eq(443), anyString()))
                .thenThrow(new RuntimeException("boom"));

        service.measureAll();

        final ArgumentCaptor<TestServerQuality> captor = ArgumentCaptor.forClass(TestServerQuality.class);
        verify(testServerQualityRepository, times(1)).save(captor.capture());
        assertFalse(captor.getValue().getReachable());
        assertNull(captor.getValue().getLatencyMs());
    }

    @Test
    void measureAll_withNoServers_doesNothing() {
        when(testServerRepository.findByServerTypeAndActiveTrue(ServerType.RMBThttp)).thenReturn(List.of());

        service.measureAll();

        verifyNoInteractions(pinger);
        verifyNoInteractions(testServerQualityRepository);
    }
}
