package at.rtr.rmbt.request;

import at.rtr.rmbt.enums.SpeedDirection;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class SpeedDetailsRequest {

    @ApiModelProperty(notes = "Actually transferred bytes by the thread", example = "38200")
    @JsonProperty(value = "bytes")
    private final Long bytes;

    @ApiModelProperty(notes = "Direction of speed test", example = "download")
    @JsonProperty(value = "direction")
    private final SpeedDirection direction;

    @ApiModelProperty(notes = "Thread number of the test which value came from", example = "10020")
    @JsonProperty(value = "thread")
    private final Long thread;

    @ApiModelProperty(notes = "Time from the test start in nanos", example = "476801245")
    @JsonProperty(value = "time")
    private final Long time;
}
