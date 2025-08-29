package at.rtr.rmbt.request;

import at.rtr.rmbt.constant.Constants;
import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import java.util.Optional;

@Getter
@EqualsAndHashCode
public class NewsParametersRequest {

    @Schema(description = "Last Uid of the news displayed to the user (Long)", example = "15")
    @JsonProperty(value = "lastNewsUid")
    private final Long lastNewsUid;

    @Schema(description = "Platform of device", example = "Android")
    @JsonAlias(value = "plattform")
    @JsonProperty(value = "platform")
    private final String platform;

    @Schema(description = "Version code from build gradle for Android devices", example = "33201")
    @JsonProperty(value = "softwareVersionCode")
    private final Long softwareVersionCode;

    @Schema(description = "Client uuid of the device", example = "68796996-5f40-11eb-ae93-0242ac130002")
    @JsonProperty(value = "uuid")
    private final String uuid;

    @Schema(description = "2 letters language code or language code with region", example = "en")
    @JsonProperty(value = "language")
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
