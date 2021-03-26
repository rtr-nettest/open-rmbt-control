package at.rtr.rmbt.request;

import at.rtr.rmbt.enums.MeasurementType;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.UUID;

@Getter
@Builder
@EqualsAndHashCode
@RequiredArgsConstructor
public class SignalRegisterRequest {

    @NotNull
    @ApiModelProperty(notes = "UUID of client", example = "68796996-5f40-11eb-ae93-0242ac130002")
    @JsonProperty(value = "uuid")
    private final UUID uuid;

    @ApiModelProperty(notes = "Time zone of client", example = "Europe/Prague")
    @JsonProperty(value = "timezone")
    private final String timezone;

    @ApiModelProperty(notes = "Time instant of client", example = "1571665024591")
    @JsonProperty(value = "time")
    private final Long time;

    @ApiModelProperty(notes = "Measurement type", example = "dedicated")
    @JsonProperty(value = "measurement_type_flag")
    private final MeasurementType measurementType;
}
