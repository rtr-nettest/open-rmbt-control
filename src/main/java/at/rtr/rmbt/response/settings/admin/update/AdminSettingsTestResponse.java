package at.rtr.rmbt.response.settings.admin.update;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Builder
@Getter
@EqualsAndHashCode
public class AdminSettingsTestResponse {

    private final String resultUrl;

    private final String resultQosUrl;

    private final String testDuration;

    private final String testNumThreads;

    private final String testNumPings;
}