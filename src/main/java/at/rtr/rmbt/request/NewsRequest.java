package at.rtr.rmbt.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import javax.validation.constraints.NotNull;
import java.util.UUID;

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
    @ApiModelProperty(notes = "2 letters language code or language code with region", example = "en")
    private final String language;

    @ApiModelProperty(notes = "News activity status", example = "true")
    private final boolean active;

    @ApiModelProperty(notes = "News force status", example = "true")
    private final boolean force;

    @ApiModelProperty(notes = "Client platform", example = "Android")
    private final String platform;

    @ApiModelProperty(notes = "Min software version", example = "1")
    private final Long minSoftwareVersion;

    @ApiModelProperty(notes = "Max software version", example = "1")
    private final Long maxSoftwareVersion;

    @ApiModelProperty(notes = "UUID of news", example = "68796996-5f40-11eb-ae93-0242ac130002")
    private final UUID uuid;
}
