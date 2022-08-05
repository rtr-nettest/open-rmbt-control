package at.rtr.rmbt.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
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
    @Schema(description = "News title", example = "en")
    @JsonProperty("title")
    private final String title;

    @NotNull
    @Schema(description = "News text", example = "en")
    @JsonProperty("content")
    private final String text;

    @NotNull
    @JsonProperty("language")
    @Schema(description = "2 letters language code or language code with region", example = "en")
    private final String language;

    @JsonProperty("active")
    @Schema(description = "News activity status", example = "true")
    private final boolean active;

    @JsonProperty("force")
    @Schema(description = "News force status", example = "true")
    private final boolean force;

    @JsonProperty("android")
    @AssertTrue(message = "Android platform must be selected")
    @Schema(description = "Client platform", example = "true")
    private final Boolean android;

    @JsonProperty("androidMinSoftwareVersion")
    @Schema(description = "Min software version", example = "1")
    private final Long androidMinSoftwareVersion;

    @JsonProperty("androidMaxSoftwareVersion")
    @Schema(description = "Max software version", example = "1")
    private final Long androidMaxSoftwareVersion;

    @JsonProperty("startDate")
    @Schema(description = "When to start showing news")
    private final ZonedDateTime startDate;

    @JsonProperty("endDate")
    @Schema(description = "When to end showing news")
    private final ZonedDateTime endDate;
}
