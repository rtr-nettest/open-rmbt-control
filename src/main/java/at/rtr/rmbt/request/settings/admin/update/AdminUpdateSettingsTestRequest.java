package at.rtr.rmbt.request.settings.admin.update;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Builder
@Getter
@EqualsAndHashCode
public class AdminUpdateSettingsTestRequest {

    @JsonProperty(value = "resultUrl")
    private final String resultUrl;

    @JsonProperty(value = "resultQosUrl")
    private final String resultQosUrl;

    @JsonProperty(value = "testDuration")
    private final String testDuration;

    @JsonProperty(value = "testNumThreads")
    private final String testNumThreads;

    @JsonProperty(value = "testNumPings")
    private final String testNumPings;
}
