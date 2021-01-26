package at.rtr.rmbt.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import at.rtr.rmbt.constant.Constants;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import java.util.Optional;

@Getter
@EqualsAndHashCode
public class NewsParametersRequest {

    private final Long lastNewsUid;

    @JsonProperty(value = "plattform")
    private final String platform;

    private final Long softwareVersionCode;

    private final String uuid;

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
