package at.rtr.rmbt.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class HistoryResponse {

    @JsonProperty(value = "devices")
    private final List<String> devices;

    @JsonProperty(value = "networks")
    private final List<String> networks;
}
