package com.rtr.nettest.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.rtr.nettest.constant.DefaultValues;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode
public class NewsParametersRequest {

    @Builder.Default
    private Long lastNewsUid = DefaultValues.NEWS_REQUEST_LAST_NEWS_UID;

    @Builder.Default
    @JsonProperty(value = "plattform")
    private String platform = DefaultValues.NEWS_REQUEST_PLATFORM;

    @Builder.Default
    private Long softwareVersionCode = DefaultValues.NEWS_REQUEST_SOFTWARE_VERSION_CODE;

    @Builder.Default
    private String uuid = DefaultValues.NEWS_REQUEST_UUID;

    @Builder.Default
    private String language = DefaultValues.NEWS_REQUEST_LANGUAGE;

}
