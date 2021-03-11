package at.rtr.rmbt.model.speed;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode
public class SpeedItem {

    @JsonProperty(value = "t")
    private Long time;

    @JsonProperty(value = "b")
    private Long bytes;
}
