package at.rtr.rmbt.request;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.UUID;

@Getter
@Builder
@EqualsAndHashCode
@RequiredArgsConstructor
public class SignalRequest {

    @NotNull
    private final UUID uuid;

    private final String timezone;

    private final Long time;
}
