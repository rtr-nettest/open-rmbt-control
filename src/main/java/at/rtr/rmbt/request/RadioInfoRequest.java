package at.rtr.rmbt.request;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class RadioInfoRequest {

    private final List<RadioCellRequest> cells;

    private final List<RadioSignalRequest> signals;
}
