package at.rtr.rmbt.service.impl;

import at.rtr.rmbt.enums.ServerType;
import at.rtr.rmbt.model.TestServer;
import at.rtr.rmbt.model.TestServerQuality;
import at.rtr.rmbt.repository.TestServerQualityRepository;
import at.rtr.rmbt.repository.TestServerRepository;
import at.rtr.rmbt.service.quality.PingOutcome;
import at.rtr.rmbt.service.quality.RmbtPinger;
import at.rtr.rmbt.service.quality.RmbtUdpPinger;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
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
    private RmbtPinger webSocketPinger;
    @Mock
    private RmbtUdpPinger udpPinger;

    @InjectMocks
    private TestServerQualityService service;

    private static final UUID SERVER_UUID = UUID.fromString("aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee");

    private static TestServer httpServer(final String v4, final String v6) {
        return TestServer.builder()
                .name("srv").uuid(SERVER_UUID).key("secret").portSsl(443)
                .webAddressIpV4(v4).webAddressIpV6(v6)
                .serverType(ServerType.RMBThttp).active(true)
                .build();
    }

    private static TestServer udpServer(final String v4, final String v6) {
        return TestServer.builder()
                .name("udp").uuid(SERVER_UUID).key("secret").port(444)
                .webAddressIpV4(v4).webAddressIpV6(v6)
                .serverType(ServerType.RMBTudp).active(true)
                .build();
    }

    @Test
    void measureAll_httpServer_pingsV4AndV6AndPersistsBothOutcomes() {
        when(testServerRepository.findByServerTypeInAndActiveTrue(anyList()))
                .thenReturn(List.of(httpServer("v4.example.com", "v6.example.com")));
        when(webSocketPinger.ping(eq("v4.example.com"), eq(443), anyString())).thenReturn(PingOutcome.reachable(12.5));
        when(webSocketPinger.ping(eq("v6.example.com"), eq(443), anyString())).thenReturn(PingOutcome.unreachable());

        service.measureAll();

        verify(webSocketPinger).ping(eq("v4.example.com"), eq(443), anyString());
        verify(webSocketPinger).ping(eq("v6.example.com"), eq(443), anyString());
        verifyNoInteractions(udpPinger);

        final ArgumentCaptor<TestServerQuality> captor = ArgumentCaptor.forClass(TestServerQuality.class);
        verify(testServerQualityRepository, times(2)).save(captor.capture());
        final Map<Integer, TestServerQuality> byProtocol = captor.getAllValues().stream()
                .collect(Collectors.toMap(TestServerQuality::getProtocol, Function.identity()));

        final TestServerQuality v4 = byProtocol.get(4);
        assertEquals(SERVER_UUID, v4.getServerUuid());
        assertTrue(v4.getReachable());
        assertEquals(12.5, v4.getLatencyMs());

        final TestServerQuality v6 = byProtocol.get(6);
        assertFalse(v6.getReachable());
        assertNull(v6.getLatencyMs());
    }

    @Test
    void measureAll_udpServer_usesUdpPingerOnPlainPort_unverifiedWhenNoPublicIp() {
        when(testServerRepository.findByServerTypeInAndActiveTrue(anyList()))
                .thenReturn(List.of(udpServer("v4.example.com", null)));
        // No public IP configured → requireIpMatch = false.
        when(udpPinger.ping(eq("v4.example.com"), eq(444), any(byte[].class), eq(false))).thenReturn(PingOutcome.reachable(7.0));

        service.measureAll();

        verify(udpPinger).ping(eq("v4.example.com"), eq(444), any(byte[].class), eq(false));
        verifyNoInteractions(webSocketPinger);

        final ArgumentCaptor<TestServerQuality> captor = ArgumentCaptor.forClass(TestServerQuality.class);
        verify(testServerQualityRepository, times(1)).save(captor.capture());
        assertEquals(4, captor.getValue().getProtocol());
        assertTrue(captor.getValue().getReachable());
        assertEquals(7.0, captor.getValue().getLatencyMs());
    }

    @Test
    void measureAll_udpServer_withConfiguredPublicIpv4_requiresIpMatch() {
        ReflectionTestUtils.setField(service, "publicIpv4", "203.0.113.5");
        when(testServerRepository.findByServerTypeInAndActiveTrue(anyList()))
                .thenReturn(List.of(udpServer("v4.example.com", null)));
        // Public IPv4 configured → requireIpMatch = true (only RR01 counts).
        when(udpPinger.ping(eq("v4.example.com"), eq(444), any(byte[].class), eq(true))).thenReturn(PingOutcome.reachable(9.0));

        service.measureAll();

        verify(udpPinger).ping(eq("v4.example.com"), eq(444), any(byte[].class), eq(true));
        verify(testServerQualityRepository, times(1)).save(any());
    }

    @Test
    void measureAll_skipsBlankAddressFamily() {
        when(testServerRepository.findByServerTypeInAndActiveTrue(anyList()))
                .thenReturn(List.of(httpServer("v4.example.com", "   ")));
        when(webSocketPinger.ping(eq("v4.example.com"), eq(443), anyString())).thenReturn(PingOutcome.reachable(5.0));

        service.measureAll();

        verify(webSocketPinger, times(1)).ping(anyString(), anyInt(), anyString());
        verify(webSocketPinger, never()).ping(eq("   "), anyInt(), anyString());
        verify(testServerQualityRepository, times(1)).save(any());
    }

    @Test
    void measureAll_recordsUnreachableWhenPingerThrows() {
        when(testServerRepository.findByServerTypeInAndActiveTrue(anyList()))
                .thenReturn(List.of(httpServer("v4.example.com", null)));
        when(webSocketPinger.ping(eq("v4.example.com"), eq(443), anyString()))
                .thenThrow(new RuntimeException("boom"));

        service.measureAll();

        final ArgumentCaptor<TestServerQuality> captor = ArgumentCaptor.forClass(TestServerQuality.class);
        verify(testServerQualityRepository, times(1)).save(captor.capture());
        assertFalse(captor.getValue().getReachable());
        assertNull(captor.getValue().getLatencyMs());
    }

    @Test
    void measureAll_withNoServers_doesNothing() {
        when(testServerRepository.findByServerTypeInAndActiveTrue(anyList())).thenReturn(List.of());

        service.measureAll();

        verifyNoInteractions(webSocketPinger);
        verifyNoInteractions(udpPinger);
        verifyNoInteractions(testServerQualityRepository);
    }
}
