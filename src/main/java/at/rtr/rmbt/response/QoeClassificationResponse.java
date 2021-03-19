package at.rtr.rmbt.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class QoeClassificationResponse {

    @JsonProperty(value = "category")
    private final String category;

    @JsonProperty(value = "classification")
    private final Integer classification;

    @JsonProperty(value = "quality")
    private final Double quality;
}
