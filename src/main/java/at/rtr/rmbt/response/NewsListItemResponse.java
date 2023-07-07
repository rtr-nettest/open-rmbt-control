package at.rtr.rmbt.response;

import at.rtr.rmbt.enums.NewsStatus;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import jakarta.validation.constraints.AssertTrue;
import java.time.ZonedDateTime;
import java.util.UUID;

@Builder
@Getter
public class NewsListItemResponse {

    @Schema(description = "Id of the news", example = "1")
    @JsonProperty("id")
    private final Long uid;

    @Schema(description = "Uuid of the news", example = "8fa5fb1c-5bf6-11eb-ae93-0242ac130002")
    @JsonProperty("uuid")
    private final UUID uuid;

    @Schema(description = "News title", example = "New version available")
    @JsonProperty("title")
    private final String title;

    @Schema(description = "News text", example = "New version features")
    @JsonProperty("content")
    private final String content;

    @Schema(description = "News language", example = "en")
    @JsonProperty("language")
    private final String language;

    @Schema(description = "Send `true` to show news or `false` keep as draft.", example = "true")
    @JsonProperty("active")
    private final Boolean active;

    @Schema(description = "Status of the news", example = "DRAFT")
    @JsonProperty("status")
    private final NewsStatus status;

    @AssertTrue
    @Schema(description = "Whether news will be shown on Android devices", example = "true")
    @JsonProperty("android")
    private final Boolean android;

    @JsonProperty("startDate")
    @Schema(description = "When to start showing news")
    private final ZonedDateTime startDate;

    @JsonProperty("endDate")
    @Schema(description = "When to end showing news")
    private final ZonedDateTime endDate;

    @JsonProperty("androidMinSoftwareVersion")
    @Schema(description = "Min software version that is eligible to see news", example = "1")
    private final Long minSoftwareVersion;

    @JsonProperty("androidMaxSoftwareVersion")
    @Schema(description = "Max software version that is eligible to see news", example = "1")
    private final Long maxSoftwareVersion;
}
