package at.rtr.rmbt.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;


@Builder
@Getter
@Setter
public class TestDistance {

    private Double maxAccuracy;

    private Double totalDistance;
}
