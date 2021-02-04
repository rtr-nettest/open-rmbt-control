package at.rtr.rmbt.response;

import at.rtr.rmbt.enums.NewsStatus;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;

import javax.validation.constraints.AssertTrue;
import java.time.ZonedDateTime;
import java.util.UUID;

@Builder
@Getter
public class NewsListItemResponse {

    @ApiModelProperty(notes = "Id of the news", example = "1")
    @JsonProperty("id")
    private Long uid;

    @ApiModelProperty(notes = "Uuid of the news", example = "8fa5fb1c-5bf6-11eb-ae93-0242ac130002")
    @JsonProperty("uuid")
    private UUID uuid;

    @ApiModelProperty(notes = "News title", example = "New version available")
    @JsonProperty("title")
    private String title;

    @ApiModelProperty(notes = "News text", example = "New version features")
    @JsonProperty("content")
    private String content;

    @ApiModelProperty(notes = "News language", example = "en")
    @JsonProperty("language")
    private String language;

    @ApiModelProperty(notes = "Send `true` to show news or `false` keep as draft.", example = "true")
    @JsonProperty("active")
    private Boolean active;

    @ApiModelProperty(notes = "Status of the news", example = "DRAFT")
    @JsonProperty("status")
    private NewsStatus status;

    @AssertTrue
    @ApiModelProperty(notes = "Whether news will be shown on Android devices", example = "true")
    @JsonProperty("android")
    private Boolean android;

    @JsonProperty("startDate")
    @ApiModelProperty(notes = "When to start showing news", example = "2020-01-01T13:00:00.123+02:00")
    private ZonedDateTime startDate;

    @JsonProperty("endDate")
    @ApiModelProperty(notes = "When to end showing news", example = "2020-01-01T13:00:00.123+02:00")
    private ZonedDateTime endDate;

    @JsonProperty("androidMinSoftwareVersion")
    @ApiModelProperty(notes = "Min software version that is eligible to see news", example = "1")
    private Long minSoftwareVersion;

    @JsonProperty("androidMaxSoftwareVersion")
    @ApiModelProperty(notes = "Max software version that is eligible to see news", example = "1")
    private Long maxSoftwareVersion;
}
