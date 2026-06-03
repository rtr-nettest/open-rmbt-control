package at.rtr.rmbt.enums;

import at.rtr.rmbt.dto.qos.*;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Objects;

/**
 * Test type enum.
 */
public enum TestType {
    HTTP_PROXY("http_proxy", "test.http", "name.http_proxy", HttpProxyResult.class),
    DNS("dns", "test.dns", "name.dns", DnsResult.class),
    TCP("tcp", "test.tcp", "name.tcp",  TcpResult.class),
    UDP("udp", "test.udp", "name.udp", UdpResult.class),
    WEBSITE("website", "test.website", "name.website", WebsiteResult.class),
    NON_TRANSPARENT_PROXY("non_transparent_proxy", "test.ntp", "name.non_transparent_proxy", NonTransparentProxyResult.class),
    TRACEROUTE("traceroute", "test.trace", "name.trace", TracerouteResult.class),
    TRACEROUTE_MASKED("traceroute_masked", "test.trace", "name.trace", TracerouteResult.class),
    VOIP("voip", "test.voip", "name.voip", VoipResult.class);

    /**
     * Creates a new TestType instance.
     *
     * @param value the Value
     * @param descriptionKey the Description key
     * @param nameKey the Name key
     * @param clazz the Clazz
     */
    TestType(String value, String descriptionKey, String nameKey, Class<? extends AbstractResult<?>> clazz) {
        this.value = value;
        this.clazz = clazz;
        this.nameKey = nameKey;
        this.descriptionKey = descriptionKey;
    }

    private String value;
    private String descriptionKey;
    private String nameKey;
    private Class<? extends AbstractResult<?>> clazz;

    @JsonValue
    public String getValue() {
        return value;
    }

    public String getDescriptionKey() {
        return descriptionKey;
    }

    public String getNameKey() {
        return nameKey;
    }

    public Class<? extends AbstractResult<?>> getClazz() {
        return clazz;
    }

    /**
     * For value.
     *
     * @param value the Value
     * @return the result
     */
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
