package at.rtr.rmbt.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import at.rtr.rmbt.constant.Constants;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import java.util.Optional;

@Getter
@EqualsAndHashCode
public class NewsParametersRequest {

    @ApiModelProperty(notes = "Last Uid of the news displayed to the user (Long)", example = "15")
    private final Long lastNewsUid;

    @JsonProperty(value = "plattform")
    @ApiModelProperty(notes = "Platform of device", example = "Android")
    private final String platform;

    @ApiModelProperty(notes = "Version code from build gradle for Android devices", example = "33201")
    private final Long softwareVersionCode;

    @ApiModelProperty(notes = "Client uuid of the device", example = "68796996-5f40-11eb-ae93-0242ac130002")
    private final String uuid;

    @ApiModelProperty(notes = "2 letters language code or language code with region", example = "en")
    private final String language;

    @Builder
    public NewsParametersRequest(Long lastNewsUid,
                                 String platform,
                                 Long softwareVersionCode,
                                 String uuid,
                                 String language) {
        this.lastNewsUid = Optional.ofNullable(lastNewsUid).orElse(Constants.NEWS_REQUEST_LAST_NEWS_UID);
        this.platform = Optional.ofNullable(platform).orElse(StringUtils.EMPTY);
        this.softwareVersionCode = Optional.ofNullable(softwareVersionCode).orElse(Constants.NEWS_REQUEST_SOFTWARE_VERSION_CODE);
        this.uuid = Optional.ofNullable(uuid).orElse(StringUtils.EMPTY);
        this.language = Optional.ofNullable(language).orElse(StringUtils.EMPTY);
    }

    public NewsParametersRequest() {
        this(null, null, null, null, null);
    }
}
