package at.rtr.rmbt.request;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AndroidPermissionStatus {

    private final String permission;

    private final boolean status;
}
