package at.rtr.rmbt.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Objects;

public enum TestType {
    HTTP_PROXY("http_proxy"),
    DNS("dns"),
    TCP("tcp"),
    UDP("udp"),
    WEBSITE("website"),
    NON_TRANSPARENT_PROXY("non_transparent_proxy"),
    TRACEROUTE("traceroute"),
    TRACEROUTE_MASKED("traceroute_masked"),
    VOIP("voip");

    TestType(String value) {
        this.value = value;
    }

    private String value;


    @JsonValue
    public String getValue() {
        return value;
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
