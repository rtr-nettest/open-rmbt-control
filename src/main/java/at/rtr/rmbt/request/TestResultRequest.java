package at.rtr.rmbt.request;

import at.rtr.rmbt.constant.Constants;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.Optional;
import java.util.UUID;

@Builder
@Getter
@EqualsAndHashCode
public class TestResultRequest {

    @JsonProperty(value = "language")
    private final String language;

    @JsonProperty(value = "test_uuid")
    private final UUID testUUID;

    @JsonProperty(value = "capabilities")
    private final CapabilitiesRequest capabilitiesRequest;

    public CapabilitiesRequest getCapabilitiesRequest() {
        return Optional.ofNullable(capabilitiesRequest).orElse(getDefaultCapabilitiesRequest());
    }

    private CapabilitiesRequest getDefaultCapabilitiesRequest() {
        return CapabilitiesRequest.builder()
                .classification(ClassificationRequest.builder().count(Constants.DEFAULT_CLASSIFICATION_COUNT).build())
                .qos(QosRequest.builder().supportsInfo(Constants.DEFAULT_QOS_SUPPORTS_INFO).build())
                .rmbtHttp(Constants.DEFAULT_RMBT_HTTP)
                .build();
    }
}
