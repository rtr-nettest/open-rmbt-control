package at.rtr.rmbt.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotNull;
import java.time.ZonedDateTime;

@Builder(toBuilder = true)
@Getter
@EqualsAndHashCode
public class NewsRequest {

    @NotNull
    @ApiModelProperty(notes = "News title", example = "en")
    @JsonProperty("title")
    private final String title;

    @NotNull
    @ApiModelProperty(notes = "News text", example = "en")
    @JsonProperty("content")
    private final String text;

    @NotNull
    @JsonProperty("language")
    @ApiModelProperty(notes = "2 letters language code or language code with region", example = "en")
    private final String language;

    @JsonProperty("active")
    @ApiModelProperty(notes = "News activity status", example = "true")
    private final boolean active;

    @JsonProperty("force")
    @ApiModelProperty(notes = "News force status", example = "true")
    private final boolean force;

    @JsonProperty("android")
    @AssertTrue(message = "Android platform must be selected")
    @ApiModelProperty(notes = "Client platform", example = "true")
    private final Boolean android;

    @JsonProperty("androidMinSoftwareVersion")
    @ApiModelProperty(notes = "Min software version", example = "1")
    private final Long androidMinSoftwareVersion;

    @JsonProperty("androidMaxSoftwareVersion")
    @ApiModelProperty(notes = "Max software version", example = "1")
    private final Long androidMaxSoftwareVersion;

    @JsonProperty("startDate")
    @ApiModelProperty(notes = "When to start showing news", example = "2020-01-01T13:00:00.123+02:00")
    private final ZonedDateTime startDate;

    @JsonProperty("endDate")
    @ApiModelProperty(notes = "When to end showing news", example = "2020-01-01T13:00:00.123+02:00")
    private final ZonedDateTime endDate;
}
