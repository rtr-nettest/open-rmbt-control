package at.rtr.rmbt.response;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ProviderResponse {

    private final Long id;

    private final String name;
}
