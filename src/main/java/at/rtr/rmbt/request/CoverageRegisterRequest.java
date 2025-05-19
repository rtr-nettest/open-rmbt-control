package at.rtr.rmbt.request;

import at.rtr.rmbt.enums.MeasurementType;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Builder
@Getter
public class CoverageRegisterRequest {


    @NotNull
    @Schema(description = "UUID of client", example = "68796996-5f40-11eb-ae93-0242ac130002")
    @JsonProperty(value = "uuid")
    private final UUID uuid;

    @Schema(description = "Time zone of client", example = "Europe/Prague")
    @JsonProperty(value = "timezone")
    private final String timezone;

    @Schema(description = "Time instant of client", example = "1571665024591")
    @JsonProperty(value = "time")
    private final Long time;

    @Schema(description = "Measurement type", example = "dedicated")
    @JsonProperty(value = "measurement_type_flag")
    private final MeasurementType measurementType;
}
