package at.rtr.rmbt.service.impl;

import at.rtr.rmbt.model.ProxyInfo;
import at.rtr.rmbt.properties.IcloudEgressProperties;
import com.google.common.net.InetAddresses;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link PrivateRelayServiceImpl}: CIDR masking / longest-prefix lookup and the
 * non-destructive staging-then-rename store. Redis is mocked, so no live instance is required.
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class PrivateRelayServiceImplTest {

    private static final char SEP = (char) 1;

    @Mock
    private StringRedisTemplate redis;

    @Mock
    @SuppressWarnings("rawtypes")
    private HashOperations hashOperations;

    private PrivateRelayServiceImpl service;

    @BeforeEach
    @SuppressWarnings("unchecked")
    void setUp() {
        when(redis.opsForHash()).thenReturn(hashOperations);
        service = new PrivateRelayServiceImpl(redis, new IcloudEgressProperties());
    }

    @Test
    @SuppressWarnings("unchecked")
    void lookup_ipv4WithinPrefix_returnsProxyInfoAndQueriesMaskedNetwork() {
        // Range 104.28.135.0/24; the query address 104.28.135.55 must be masked to 104.28.135.0.
        when(hashOperations.get("proxy:relay:v4", "#lengths")).thenReturn("24");
        when(hashOperations.multiGet(eq("proxy:relay:v4"), any(List.class)))
                .thenReturn(List.of("AT" + SEP + "AT-09" + SEP + "Vienna" + SEP + "false"));

        Optional<ProxyInfo> result = service.lookup(InetAddresses.forString("104.28.135.55"));

        assertThat(result).isPresent();
        assertThat(result.get().proxyCountry()).isEqualTo("AT");
        assertThat(result.get().proxyRegion()).isEqualTo("AT-09");
        assertThat(result.get().proxyCity()).isEqualTo("Vienna");
        assertThat(result.get().ipv6()).isFalse();

        // 104.28.135.0 -> bytes 68 1c 87 00
        ArgumentCaptor<List<Object>> fields = ArgumentCaptor.forClass(List.class);
        verify(hashOperations).multiGet(eq("proxy:relay:v4"), fields.capture());
        assertThat(fields.getValue()).containsExactly("24|681c8700");
    }

    @Test
    @SuppressWarnings("unchecked")
    void lookup_prefersLongestPrefix() {
        when(hashOperations.get("proxy:relay:v4", "#lengths")).thenReturn("24,32");
        // multiGet must be probed longest-first: /32 then /24. First non-null wins.
        when(hashOperations.multiGet(eq("proxy:relay:v4"), any(List.class)))
                .thenReturn(java.util.Arrays.asList("DE" + SEP + "DE-NW" + SEP + "Bocholt" + SEP + "false", null));

        Optional<ProxyInfo> result = service.lookup(InetAddresses.forString("104.28.135.1"));

        assertThat(result).isPresent();
        assertThat(result.get().proxyCity()).isEqualTo("Bocholt");

        ArgumentCaptor<List<Object>> fields = ArgumentCaptor.forClass(List.class);
        verify(hashOperations).multiGet(eq("proxy:relay:v4"), fields.capture());
        // /32 (full address) before /24 (masked): longest prefix first.
        assertThat(fields.getValue()).containsExactly("32|681c8701", "24|681c8700");
    }

    @Test
    @SuppressWarnings("unchecked")
    void lookup_ipv6UsesV6HashAndFullByteMask() {
        when(hashOperations.get("proxy:relay:v6", "#lengths")).thenReturn("64");
        when(hashOperations.multiGet(eq("proxy:relay:v6"), any(List.class)))
                .thenReturn(List.of("DE" + SEP + "DE-NW" + SEP + "Bocholt" + SEP + "true"));

        Optional<ProxyInfo> result = service.lookup(InetAddresses.forString("2606:54c3:0:1690::abcd"));

        assertThat(result).isPresent();
        assertThat(result.get().ipv6()).isTrue();
        // /64 keeps the first 8 bytes, zeroes the rest: 2606:54c3:0000:1690::
        ArgumentCaptor<List<Object>> fields = ArgumentCaptor.forClass(List.class);
        verify(hashOperations).multiGet(eq("proxy:relay:v6"), fields.capture());
        assertThat(fields.getValue()).containsExactly("64|260654c300001690" + "0000000000000000");
    }

    @Test
    void lookup_noData_returnsEmpty() {
        when(hashOperations.get("proxy:relay:v4", "#lengths")).thenReturn(null);

        assertThat(service.lookup(InetAddresses.forString("8.8.8.8"))).isEmpty();
    }

    @Test
    @SuppressWarnings("unchecked")
    void lookup_noMatch_returnsEmpty() {
        when(hashOperations.get("proxy:relay:v4", "#lengths")).thenReturn("24");
        when(hashOperations.multiGet(eq("proxy:relay:v4"), any(List.class)))
                .thenReturn(java.util.Collections.singletonList(null));

        assertThat(service.lookup(InetAddresses.forString("8.8.8.8"))).isEmpty();
    }

    @Test
    void lookup_redisFailure_isSwallowed() {
        when(hashOperations.get(anyString(), any())).thenThrow(new RuntimeException("redis down"));

        assertThat(service.lookup(InetAddresses.forString("8.8.8.8"))).isEmpty();
    }
}
