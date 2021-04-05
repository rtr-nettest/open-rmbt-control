package at.rtr.rmbt.enums;

import at.rtr.rmbt.dto.qos.*;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Objects;

public enum TestType {
    HTTP_PROXY("http_proxy", HttpProxyResult.class),
    DNS("dns", DnsResult.class),
    TCP("tcp", TcpResult.class),
    UDP("udp", UdpResult.class),
    WEBSITE("website", WebsiteResult.class),
    NON_TRANSPARENT_PROXY("non_transparent_proxy", NonTransparentProxyResult.class),
    TRACEROUTE("traceroute", TracerouteResult.class),
    TRACEROUTE_MASKED("traceroute_masked", TracerouteResult.class),
    VOIP("voip", VoipResult.class);

    TestType(String value, Class<? extends AbstractResult<?>> clazz) {
        this.value = value;
        this.clazz = clazz;
    }

    private String value;
    private Class<? extends AbstractResult<?>> clazz;

    @JsonValue
    public String getValue() {
        return value;
    }

    public Class<? extends AbstractResult<?>> getClazz() {
        return clazz;
    }

    @JsonCreator
    public static TestType forValue(String value) {
        if (Objects.isNull(value)) {
            return null;
        }
        for (TestType testType : values()) {
            if (testType.value.equals(value.toLowerCase()))
                return testType;
        }
        throw new IllegalArgumentException();
    }
}
