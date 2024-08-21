package at.rtr.rmbt.request;

import at.rtr.rmbt.enums.NetworkGroupName;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
public class RadioCellRequest {

    @JsonProperty(value = "active")
    private final boolean active;

    @JsonProperty(value = "area_code")
    private final Long areaCode;

    @JsonProperty(value = "location_id")
    private final Long locationId;

    @JsonProperty(value = "mcc")
    private final Long mcc;

    @JsonProperty(value = "mnc")
    private final Long mnc;

    @JsonProperty(value = "primary_scrambling_code")
    private final Integer primaryScramblingCode;

    @JsonProperty(value = "primary_data_subscription")
    private final String primaryDataSubscription;

    @JsonProperty(value = "registered")
    private final boolean registered;

    @JsonProperty(value = "technology")
    private final NetworkGroupName technology;

    @JsonProperty(value = "uuid")
    private final UUID uuid;

    @JsonProperty(value = "channel_number")
    private final Integer channelNumber;

    @JsonProperty(value = "cell_state")
    private final String cellState;
}
