package at.rtr.rmbt.request;

import at.rtr.rmbt.enums.NetworkGroupName;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
public class RadioCellRequest {

    private final boolean active;

    @JsonProperty(value = "area_code")
    private final Long areaCode;

    @JsonProperty(value = "location_id")
    private final Long locationId;

    @JsonProperty(value = "mmc")
    private final Long mcc;

    @JsonProperty(value = "mnc")
    private final Long mnc;

    @JsonProperty(value = "primary_scrambling_code")
    private final Long primaryScramblingCode;

    private final boolean registered;

    private final NetworkGroupName technology;

    private final UUID uuid;

    @JsonProperty(value = "channel_number")
    private final Long channelNumber;
}
